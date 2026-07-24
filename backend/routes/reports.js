const express = require('express');
const router = express.Router();
const db = require('../config/db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');

router.post('/', auth, async (req, res) => {
  try {
    const { pdf_data } = req.body;
    if (!pdf_data) {
      return res.status(400).json({ message: 'No pdf data provided' });
    }

    // Extract base64
    const base64Parts = pdf_data.split('base64,');
    if (base64Parts.length !== 2) {
      return res.status(400).json({ message: 'Invalid pdf data' });
    }

    const buffer = Buffer.from(base64Parts[1], 'base64');
    
    // Create reports directory if it doesn't exist
    const reportsDir = path.join(__dirname, '..', 'reports');
    if (!fs.existsSync(reportsDir)){
        fs.mkdirSync(reportsDir);
    }

    const filename = `${req.user.id}_${Date.now()}.pdf`;
    const filepath = path.join(reportsDir, filename);

    fs.writeFileSync(filepath, buffer);

    const reportUrl = `/reports/${filename}`;
    const reportId = uuidv4();

    await db.query(
      `INSERT INTO reports (id, user_id, report_url) VALUES (?, ?, ?)`,
      [reportId, req.user.id, reportUrl]
    );

    res.json({ message: 'Report saved successfully', url: reportUrl });
  } catch (error) {
    console.error('Error saving report:', error);
    res.status(500).json({ message: 'Server error saving report' });
  }
});

router.get('/', auth, async (req, res) => {
    try {
        const [rows] = await db.query('SELECT * FROM reports WHERE user_id = ? ORDER BY created_at DESC', [req.user.id]);
        res.json(rows);
    } catch (error) {
        console.error('Error fetching reports:', error);
        res.status(500).json({ message: 'Server error fetching reports' });
    }
});

module.exports = router;
