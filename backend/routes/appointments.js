const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');

function toAppointment(row) {
  return {
    id: row.id,
    user_id: row.user_id,
    doctor_name: row.doctor_name,
    date: row.appointment_date,
    time: row.appointment_time,
    reason: row.reason,
    status: row.status,
    created_at: row.created_at
  };
}

router.get('/', auth, async (req, res) => {
  try {
    const [rows] = await db.query(
      `SELECT id, user_id, doctor_name, appointment_date, appointment_time, reason, status, created_at
       FROM appointments
       WHERE user_id = ?
       ORDER BY created_at DESC`,
      [req.user.id]
    );

    res.json(rows.map(toAppointment));
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

router.post('/', auth, async (req, res) => {
  const { doctor_name, date, time, reason } = req.body;

  if (!doctor_name || !date || !time) {
    return res.status(400).json({ message: 'doctor_name, date, and time are required' });
  }

  try {
    const id = uuidv4();
    const status = 'Confirmed';

    await db.query(
      `INSERT INTO appointments
       (id, user_id, doctor_name, appointment_date, appointment_time, reason, status)
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [id, req.user.id, doctor_name, date, time, reason || null, status]
    );

    res.status(201).json({
      id,
      user_id: req.user.id,
      doctor_name,
      date,
      time,
      reason: reason || null,
      status
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
