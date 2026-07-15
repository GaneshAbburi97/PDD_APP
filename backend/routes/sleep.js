const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');

router.get('/', auth, async (req, res) => {
  try {
    const [rows] = await db.query('SELECT * FROM sleep_records WHERE user_id = ? ORDER BY timestamp DESC', [req.user.id]);
    res.json(rows);
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

router.post('/', auth, async (req, res) => {
  const { date, sleep_hours, sleep_quality, jaw_clenching, morning_stiffness, wakeup_feeling, notes, timestamp } = req.body;
  try {
    const id = uuidv4();
    await db.query(
      'INSERT INTO sleep_records (id, user_id, date, sleep_hours, sleep_quality, jaw_clenching, morning_stiffness, wakeup_feeling, notes, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
      [id, req.user.id, date, sleep_hours, sleep_quality, jaw_clenching, morning_stiffness, wakeup_feeling, notes, timestamp]
    );
    res.status(201).json({ id, user_id: req.user.id, date, sleep_hours, sleep_quality, jaw_clenching, morning_stiffness, wakeup_feeling, notes, timestamp });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
