const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');

router.get('/', auth, async (req, res) => {
  try {
    const [rows] = await db.query('SELECT * FROM wellness_records WHERE user_id = ? ORDER BY timestamp DESC', [req.user.id]);
    res.json(rows);
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

router.post('/', auth, async (req, res) => {
  const { date, sleep_quality, jaw_stiffness, teeth_grinding, mood, water_intake, energy_level, notes, timestamp } = req.body;
  try {
    const id = uuidv4();
    await db.query(
      'INSERT INTO wellness_records (id, user_id, date, sleep_quality, jaw_stiffness, teeth_grinding, mood, water_intake, energy_level, notes, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
      [
        id, 
        req.user.id, 
        date, 
        sleep_quality || 'Good', 
        jaw_stiffness || 'None', 
        teeth_grinding || false, 
        mood || 'Neutral', 
        water_intake || 0, 
        energy_level || 5, 
        notes || '', 
        timestamp || Date.now()
      ]
    );
    res.status(201).json({ id, user_id: req.user.id, date, mood, timestamp });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
