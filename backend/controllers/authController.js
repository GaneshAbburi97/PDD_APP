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
    await db.query('DELETE FROM users WHERE id = ?', [req.user.id]);
    res.json({ message: 'Account deleted' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error during account deletion' });
  }
};
