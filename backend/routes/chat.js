const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const Groq = require('groq-sdk');

const groq = new Groq({ apiKey: process.env.GROQ_API_KEY });

router.post('/', auth, async (req, res) => {
  const { messages } = req.body;
  if (!messages || !Array.isArray(messages)) {
    return res.status(400).json({ error: 'Messages array is required' });
  }

  try {
    const chatCompletion = await groq.chat.completions.create({
      messages: [
        { role: 'system', content: 'You are a helpful and knowledgeable clinical assistant for TMD (Temporomandibular Disorders) patients. Provide clear, empathetic, and evidence-based advice about jaw pain, exercises, sleep, and stress management. When giving important instructions, prefix the line with [INFO], [SUCCESS], [WARNING], [ALERT], or [REC] so the frontend can style it appropriately.' },
        ...messages
      ],
      model: 'llama-3.1-8b-instant',
      temperature: 0.5,
      max_tokens: 1024,
      top_p: 1,
    });

    const reply = chatCompletion.choices[0]?.message?.content || "Sorry, I couldn't process that.";
    res.json({ reply });
  } catch (error) {
    console.error('Groq Error:', error);
    res.status(500).json({ error: 'Failed to communicate with AI service' });
  }
});

module.exports = router;
