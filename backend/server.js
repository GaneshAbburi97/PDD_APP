const express = require('express');
const cors = require('cors');
const path = require('path');
require('dotenv').config();

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Serve static files
app.use('/videos', express.static(path.join(__dirname, 'videos')));
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));
app.use('/reports', express.static(path.join(__dirname, 'reports')));

// Routes
const authRoutes = require('./routes/auth');
const painRoutes = require('./routes/pain');
const sleepRoutes = require('./routes/sleep');
const exerciseRoutes = require('./routes/exercise');
const wellnessRoutes = require('./routes/wellness');
const assessmentRoutes = require('./routes/assessment');
const appointmentRoutes = require('./routes/appointments');
const feedbackRoutes = require('./routes/feedback');
const chatRoutes = require('./routes/chat');
const contactRoutes = require('./routes/contact');

app.use('/api/auth', authRoutes);
app.use('/api/pain', painRoutes);
app.use('/api/sleep', sleepRoutes);
app.use('/api/exercise', exerciseRoutes);
app.use('/api/wellness', wellnessRoutes);
app.use('/api/assessment', assessmentRoutes);
app.use('/api/appointments', appointmentRoutes);
app.use('/api/feedback', feedbackRoutes);
app.use('/api/chat', chatRoutes);
app.use('/api/contact', contactRoutes);

const PORT = process.env.PORT || 5000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server running on port ${PORT}`);
});
