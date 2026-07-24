const db = require('../config/db');
const { OAuth2Client } = require('google-auth-library');
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('uuid');

const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

function signToken(user) {
  return jwt.sign(
    { user: { id: user.id, email: user.email } },
    process.env.JWT_SECRET,
    { expiresIn: '30d' }
  );
}

function publicUser(user) {
  return {
    id: user.id,
    name: user.name,
    email: user.email,
    auth_provider: user.auth_provider,
    profile_image_path: user.profile_image_path,
    height_cm: user.height_cm,
    weight_kg: user.weight_kg,
    created_at: user.created_at
  };
}

exports.googleLogin = async (req, res) => {
  const { idToken } = req.body;
  try {
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });
    const payload = ticket.getPayload();
    const { sub, email, name, picture } = payload; // sub is google user id

    // Check if user exists
    const [rows] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
    let user = rows[0];

    if (!user) {
      // Create new user
      const newId = uuidv4();
      await db.query(
        'INSERT INTO users (id, name, email, auth_provider, profile_image_path) VALUES (?, ?, ?, ?, ?)',
        [newId, name, email, 'google', picture]
      );
      user = { id: newId, name, email, auth_provider: 'google', profile_image_path: picture };
    }

    const token = signToken(user);

    res.json({ token, user: publicUser(user) });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error during Google Login' });
  }
};

exports.login = async (req, res) => {
  const { email, password } = req.body;
  try {
    const [rows] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
    if (rows.length === 0) return res.status(400).json({ message: 'Invalid credentials' });
    
    const user = rows[0];
    if (user.auth_provider !== 'local' && !user.password_hash) {
      return res.status(400).json({ message: 'Please login using ' + user.auth_provider });
    }
    
    const bcrypt = require('bcryptjs');
    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) return res.status(400).json({ message: 'Invalid credentials' });
    
    const token = signToken(user);
    res.json({ token, user: publicUser(user) });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error during login' });
  }
};

exports.register = async (req, res) => {
  const { name, email, password } = req.body;
  try {
    const [rows] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
    if (rows.length > 0) return res.status(400).json({ message: 'User already exists' });
    
    const bcrypt = require('bcryptjs');
    const salt = await bcrypt.genSalt(10);
    const password_hash = await bcrypt.hash(password, salt);
    
    const newId = uuidv4();
    await db.query(
      'INSERT INTO users (id, name, email, auth_provider, password_hash) VALUES (?, ?, ?, ?, ?)',
      [newId, name, email, 'local', password_hash]
    );
    
    const userPayload = { id: newId, name, email, auth_provider: 'local' };
    const token = signToken(userPayload);
    res.json({ token, user: userPayload });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error during registration' });
  }
};

exports.getProfile = async (req, res) => {
  try {
    const [rows] = await db.query('SELECT id, name, email, profile_image_path, height_cm, weight_kg, created_at FROM users WHERE id = ?', [req.user.id]);
    if (rows.length === 0) return res.status(404).json({ message: 'User not found' });
    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.updateProfile = async (req, res) => {
  const { name, email, profile_image_path, height_cm, weight_kg } = req.body;

  try {
    const [existing] = await db.query('SELECT id FROM users WHERE email = ? AND id <> ?', [email, req.user.id]);
    if (existing.length > 0) {
      return res.status(400).json({ message: 'Email is already in use' });
    }

    await db.query(
      `UPDATE users
       SET name = ?, email = ?, profile_image_path = ?, height_cm = ?, weight_kg = ?
       WHERE id = ?`,
      [
        name,
        email,
        profile_image_path || null,
        height_cm ?? null,
        weight_kg ?? null,
        req.user.id
      ]
    );

    const [rows] = await db.query(
      'SELECT id, name, email, auth_provider, profile_image_path, height_cm, weight_kg, created_at FROM users WHERE id = ?',
      [req.user.id]
    );

    if (rows.length === 0) return res.status(404).json({ message: 'User not found' });
    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error during profile update' });
  }
};

exports.deleteAccount = async (req, res) => {
  try {
    const [users] = await db.query('SELECT name, email FROM users WHERE id = ?', [req.user.id]);
    if (users.length > 0) {
      const user = users[0];
      await db.query(
        'INSERT INTO deleted_accounts (original_user_id, name, email) VALUES (?, ?, ?)',
        [req.user.id, user.name, user.email]
      );
    }

    const userId = req.user.id;
    // Delete child records manually
    await db.query('DELETE FROM assessment_records WHERE user_id = ?', [userId]).catch(() => {});
    await db.query('DELETE FROM exercise_records WHERE user_id = ?', [userId]).catch(() => {});
    await db.query('DELETE FROM feedback WHERE user_id = ?', [userId]).catch(() => {});
    await db.query('DELETE FROM pain_records WHERE user_id = ?', [userId]).catch(() => {});
    await db.query('DELETE FROM reports WHERE user_id = ?', [userId]).catch(() => {});
    await db.query('DELETE FROM sleep_records WHERE user_id = ?', [userId]).catch(() => {});
    await db.query('DELETE FROM wellness_records WHERE user_id = ?', [userId]).catch(() => {});

    await db.query('DELETE FROM users WHERE id = ?', [userId]);
    res.json({ message: 'Account deleted' });
  } catch (err) {
    console.error('Delete account error:', err);
    res.status(500).json({ message: 'Server error during account deletion' });
  }
};

const nodemailer = require('nodemailer');
const bcrypt = require('bcryptjs');

// Create reusable transporter object using TLS (Port 587) and forcing IPv4
const transporter = nodemailer.createTransport({
  host: 'smtp.gmail.com',
  port: 587,
  secure: false,
  requireTLS: true,
  family: 4, // Force IPv4 to prevent ENETUNREACH on IPv6 networks
  auth: {
    user: process.env.GMAIL_USER,
    pass: process.env.GMAIL_APP_PASSWORD
  }
});

exports.forgotPassword = async (req, res) => {
  const { email } = req.body;
  
  if (!email) {
    return res.status(400).json({ message: 'Email is required' });
  }

  try {
    const [users] = await db.query('SELECT id, name FROM users WHERE email = ?', [email]);
    
    if (users.length > 0) {
      const user = users[0];
      // Generate a 6-digit numeric OTP
      const otp = Math.floor(100000 + Math.random() * 900000).toString();
      
      // OTP expires in 15 minutes
      const expiry = new Date(Date.now() + 15 * 60 * 1000);
      
      await db.query(
        'UPDATE users SET reset_otp = ?, reset_otp_expiry = ? WHERE id = ?',
        [otp, expiry, user.id]
      );

      const mailOptions = {
        from: `"TMD Health App" <${process.env.GMAIL_USER}>`,
        to: email,
        subject: 'Password Reset OTP - TMD Health App',
        html: `
          <div style="font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px;">
            <h2 style="color: #2563eb;">Password Reset Request</h2>
            <p>Hi ${user.name},</p>
            <p>We received a request to reset your password. Use the OTP below to set a new password:</p>
            <div style="background-color: #f3f4f6; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px; border-radius: 4px; margin: 20px 0;">
              ${otp}
            </div>
            <p>This code will expire in 15 minutes.</p>
            <p>If you didn't request this, you can safely ignore this email.</p>
          </div>
        `
      };

      try {
        await transporter.sendMail(mailOptions);
        console.log(`[SUCCESS] Reset email sent to ${email}`);
      } catch (emailError) {
        // Many local ISPs block outbound SMTP ports (465/587). 
        // We catch this gracefully so the user can still test the app!
        console.warn(`\n[WARNING] Could not send email via SMTP (Likely blocked by your local network/ISP firewall).`);
        console.warn(`[DEV MODE] Your Password Reset OTP for ${email} is: ===> ${otp} <===\n`);
      }
    }
    
    // Always return success to prevent email enumeration, and so the UI can proceed
    res.json({ message: 'Password reset instructions have been processed.' });
  } catch (error) {
    console.error('Forgot password error:', error);
    res.status(500).json({ message: 'Server error while processing request' });
  }
};

exports.verifyOTP = async (req, res) => {
  const { email, otp } = req.body;
  
  if (!email || !otp) {
    return res.status(400).json({ message: 'Email and OTP are required' });
  }

  try {
    const [users] = await db.query(
      'SELECT id, reset_otp_expiry FROM users WHERE email = ? AND reset_otp = ?', 
      [email, otp]
    );

    if (users.length === 0) {
      return res.status(400).json({ message: 'Invalid or incorrect OTP.' });
    }

    const user = users[0];
    if (new Date() > new Date(user.reset_otp_expiry)) {
      return res.status(400).json({ message: 'OTP has expired. Please request a new one.' });
    }

    res.json({ message: 'OTP verified successfully' });
  } catch (error) {
    console.error('Verify OTP error:', error);
    res.status(500).json({ message: 'Server error while verifying OTP' });
  }
};

exports.resetPassword = async (req, res) => {
  const { email, otp, newPassword } = req.body;
  
  if (!email || !otp || !newPassword) {
    return res.status(400).json({ message: 'All fields are required' });
  }

  try {
    // Verify OTP again just to be safe
    const [users] = await db.query(
      'SELECT id, reset_otp_expiry FROM users WHERE email = ? AND reset_otp = ?', 
      [email, otp]
    );

    if (users.length === 0) {
      return res.status(400).json({ message: 'Invalid OTP.' });
    }

    const user = users[0];
    if (new Date() > new Date(user.reset_otp_expiry)) {
      return res.status(400).json({ message: 'OTP has expired.' });
    }

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(newPassword, salt);

    // Update password and clear OTP fields
    await db.query(
      'UPDATE users SET password_hash = ?, reset_otp = NULL, reset_otp_expiry = NULL WHERE id = ?',
      [hashedPassword, user.id]
    );

    res.json({ message: 'Password has been reset successfully.' });
  } catch (error) {
    console.error('Reset password error:', error);
    res.status(500).json({ message: 'Server error while resetting password' });
  }
};
