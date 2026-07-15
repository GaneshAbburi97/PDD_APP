const baseUrl = process.argv[2] || process.env.API_BASE_URL || 'http://127.0.0.1:5000';

async function request(path, options = {}) {
  const response = await fetch(`${baseUrl}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    }
  });

  const body = await response.json();

  if (!response.ok) {
    throw new Error(`${options.method || 'GET'} ${path} failed: ${response.status} ${JSON.stringify(body)}`);
  }

  return body;
}

async function smokeTest() {
  const email = `smoke_${Date.now()}@test.local`;

  const auth = await request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ name: 'Smoke Test', email, password: 'password123' })
  });

  const headers = { Authorization: `Bearer ${auth.token}` };

  await request('/api/sleep', {
    method: 'POST',
    headers,
    body: JSON.stringify({
      date: '09-07-2026',
      sleep_hours: 7.5,
      sleep_quality: 'Good',
      jaw_clenching: true,
      morning_stiffness: 'Mild',
      wakeup_feeling: 'Rested',
      notes: 'Smoke test',
      timestamp: Date.now()
    })
  });

  const sleepRecords = await request('/api/sleep', { headers });
  const firstSleep = sleepRecords[0];

  await request('/api/appointments', {
    method: 'POST',
    headers,
    body: JSON.stringify({
      doctor_name: 'Dr. Smoke Test',
      date: '2026-07-09',
      time: '10:00 AM',
      reason: 'Smoke test booking'
    })
  });

  const appointments = await request('/api/appointments', { headers });

  const feedback = await request('/api/feedback', {
    method: 'POST',
    headers,
    body: JSON.stringify({ name: 'Smoke Test', message: 'Smoke feedback' })
  });

  console.log({
    baseUrl,
    registered: auth.user?.email === email,
    sleepBooleanType: typeof firstSleep?.jaw_clenching,
    appointments: appointments.length,
    feedbackSaved: Boolean(feedback.id)
  });
}

smokeTest().catch((error) => {
  console.error(error);
  process.exit(1);
});
