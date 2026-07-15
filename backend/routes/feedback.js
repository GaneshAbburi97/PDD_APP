const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');

router.post('/', auth, async (req, res) => {
  const { name, message } = req.body;

  if (!message || !message.trim()) {
    return res.status(400).json({ message: 'Feedback message is required' });
  }

  try {
    const id = uuidv4();

    await db.query(
      'INSERT INTO feedback (id, user_id, name, message) VALUES (?, ?, ?, ?)',
      [id, req.user.id, name || null, message.trim()]
    );

    res.status(201).json({ id, user_id: req.user.id });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
