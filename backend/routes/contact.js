const express = require('express');
const router = express.Router();
const nodemailer = require('nodemailer');

// Create Gmail transporter
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: process.env.GMAIL_USER,
    pass: process.env.GMAIL_APP_PASSWORD
  }
});

// POST /api/contact - Send support email
router.post('/', async (req, res) => {
  try {
    const { name, email, subject, message } = req.body;

    if (!name || !email || !subject || !message) {
      return res.status(400).json({ error: 'All fields are required' });
    }

    // Email to the support team (you)
    const mailToSupport = {
      from: `"TMD Self-Care App" <${process.env.GMAIL_USER}>`,
      to: process.env.GMAIL_USER,
      replyTo: email,
      subject: `[TMD Support] ${subject}`,
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
          <div style="background: linear-gradient(135deg, #6366f1, #4f46e5); padding: 20px; color: white;">
            <h2 style="margin: 0;">New Support Request</h2>
            <p style="margin: 5px 0 0 0; opacity: 0.9;">TMD Self-Care Application</p>
          </div>
          <div style="padding: 20px;">
            <table style="width: 100%; border-collapse: collapse;">
              <tr>
                <td style="padding: 8px 0; font-weight: bold; color: #555; width: 100px;">From:</td>
                <td style="padding: 8px 0;">${name}</td>
              </tr>
              <tr>
                <td style="padding: 8px 0; font-weight: bold; color: #555;">Email:</td>
                <td style="padding: 8px 0;"><a href="mailto:${email}">${email}</a></td>
              </tr>
              <tr>
                <td style="padding: 8px 0; font-weight: bold; color: #555;">Subject:</td>
                <td style="padding: 8px 0;">${subject}</td>
              </tr>
            </table>
            <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 15px 0;">
            <h3 style="color: #333; margin-bottom: 10px;">Message:</h3>
            <div style="background: #f9f9f9; padding: 15px; border-radius: 6px; color: #444; line-height: 1.6;">
              ${message.replace(/\n/g, '<br>')}
            </div>
          </div>
          <div style="background: #f5f5f5; padding: 12px 20px; text-align: center; font-size: 12px; color: #888;">
            Sent from TMD Self-Care App • ${new Date().toLocaleString()}
          </div>
        </div>
      `
    };

    // Confirmation email to the patient
    const mailToPatient = {
      from: `"TMD Self-Care Support" <${process.env.GMAIL_USER}>`,
      to: email,
      subject: `We received your message — ${subject}`,
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
          <div style="background: linear-gradient(135deg, #6366f1, #4f46e5); padding: 20px; color: white;">
            <h2 style="margin: 0;">Thank You, ${name}!</h2>
            <p style="margin: 5px 0 0 0; opacity: 0.9;">We've received your support request</p>
          </div>
          <div style="padding: 20px;">
            <p style="color: #444; line-height: 1.6;">
              Hi <strong>${name}</strong>,
            </p>
            <p style="color: #444; line-height: 1.6;">
              We've received your message regarding <strong>"${subject}"</strong> and our team will get back to you within 24 hours.
            </p>
            <div style="background: #f9f9f9; padding: 15px; border-radius: 6px; margin: 15px 0;">
              <p style="margin: 0 0 5px 0; font-weight: bold; color: #555;">Your message:</p>
              <p style="margin: 0; color: #666; line-height: 1.6;">${message.replace(/\n/g, '<br>')}</p>
            </div>
            <p style="color: #444; line-height: 1.6;">
              In the meantime, feel free to check our <strong>FAQ section</strong> in the app for quick answers.
            </p>
            <p style="color: #444;">
              Best regards,<br>
              <strong>TMD Self-Care Support Team</strong>
            </p>
          </div>
          <div style="background: #f5f5f5; padding: 12px 20px; text-align: center; font-size: 12px; color: #888;">
            TMD Self-Care App • ganeshabburi97@gmail.com
          </div>
        </div>
      `
    };

    // Send both emails
    await transporter.sendMail(mailToSupport);
    await transporter.sendMail(mailToPatient);

    res.json({ success: true, message: 'Email sent successfully!' });

  } catch (error) {
    console.error('Email send error:', error);
    res.status(500).json({ error: 'Failed to send email. Please try again.' });
  }
});

module.exports = router;
