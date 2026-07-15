const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const auth = require('../middleware/auth');

router.post('/google', authController.googleLogin);
router.post('/login', authController.login);
router.post('/register', authController.register);
router.get('/profile', auth, authController.getProfile);
router.put('/profile', auth, authController.updateProfile);
router.delete('/profile', auth, authController.deleteAccount);

module.exports = router;
