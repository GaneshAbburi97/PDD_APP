const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');

router.get('/', auth, async (req, res) => {
  try {
    const [rows] = await db.query('SELECT * FROM assessment_records WHERE user_id = ? ORDER BY timestamp DESC', [req.user.id]);
    res.json(rows);
  } catch (err) {
    res.status(500).json({ message: 'Server error' });
  }
});

router.post('/', auth, async (req, res) => {
  const {
    timestamp, date, q1_teeth_grinding, q2_jaw_clenching, q3_chew_gum, q4_bite_nails, q5_jaw_clicking,
    q6_difficulty_chewing, q7_morning_stiffness, q8_frequent_headaches, q9_sleep_less_than_6_hours,
    q10_high_stress, q11_poor_posture, q12_one_side_chewing, sleep_duration, water_intake, stress_frequency,
    jaw_pain_frequency, exercise_consistency, smart_analysis
  } = req.body;
  try {
    const id = uuidv4();
    await db.query(
      `INSERT INTO assessment_records (
        id, user_id, timestamp, date, q1_teeth_grinding, q2_jaw_clenching, q3_chew_gum, q4_bite_nails,
        q5_jaw_clicking, q6_difficulty_chewing, q7_morning_stiffness, q8_frequent_headaches, q9_sleep_less_than_6_hours,
        q10_high_stress, q11_poor_posture, q12_one_side_chewing, sleep_duration, water_intake, stress_frequency,
        jaw_pain_frequency, exercise_consistency, smart_analysis
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        id, req.user.id, timestamp, date, q1_teeth_grinding, q2_jaw_clenching, q3_chew_gum, q4_bite_nails,
        q5_jaw_clicking, q6_difficulty_chewing, q7_morning_stiffness, q8_frequent_headaches, q9_sleep_less_than_6_hours,
        q10_high_stress, q11_poor_posture, q12_one_side_chewing, sleep_duration, water_intake, stress_frequency,
        jaw_pain_frequency, exercise_consistency, smart_analysis
      ]
    );
    res.status(201).json({ id, user_id: req.user.id });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
