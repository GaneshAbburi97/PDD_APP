const db = require('./config/db');
async function test() {
  try {
    const [rows] = await db.query('SELECT 1');
    console.log("DB connection successful:", rows);
    process.exit(0);
  } catch (err) {
    console.error("DB connection failed:", err);
    process.exit(1);
  }
}
test();
