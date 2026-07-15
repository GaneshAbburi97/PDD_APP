const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');

router.get('/', auth, async (req, res) => {
  try {
    const [rows] = await db.query('SELECT * FROM pain_records WHERE user_id = ? ORDER BY timestamp DESC', [req.user.id]);
    res.json(rows);
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

router.post('/', auth, async (req, res) => {
  const { date, pain_level, stress_level, location, type, timestamp } = req.body;
  try {
    const id = uuidv4();
    await db.query(
      'INSERT INTO pain_records (id, user_id, date, pain_level, stress_level, location, type, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
      [id, req.user.id, date, pain_level, stress_level, location, type || 'Dull', timestamp]
    );
    res.status(201).json({ id, user_id: req.user.id, date, pain_level, stress_level, location, type, timestamp });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

router.put('/:id', auth, async (req, res) => {
  const { date, pain_level, stress_level, location, type, timestamp } = req.body;

  try {
    const [result] = await db.query(
      `UPDATE pain_records
       SET date = ?, pain_level = ?, stress_level = ?, location = ?, type = ?, timestamp = ?
       WHERE id = ? AND user_id = ?`,
      [date, pain_level, stress_level, location, type || 'Dull', timestamp, req.params.id, req.user.id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: 'Pain record not found' });
    }

    res.json({
      id: req.params.id,
      user_id: req.user.id,
      date,
      pain_level,
      stress_level,
      location,
      type: type || 'Dull',
      timestamp
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
