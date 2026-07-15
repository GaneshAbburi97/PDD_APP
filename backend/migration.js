const mysql = require('mysql2/promise');
require('dotenv').config();

async function migrate() {
  try {
    const connection = await mysql.createConnection({
      host: process.env.DB_HOST,
      user: process.env.DB_USER,
      password: process.env.DB_PASSWORD,
      database: process.env.DB_NAME
    });

    // Check if column exists first
    const [columns] = await connection.query(`SHOW COLUMNS FROM users LIKE 'password_hash'`);
    if (columns.length === 0) {
      await connection.query(`ALTER TABLE users ADD COLUMN password_hash VARCHAR(255)`);
      console.log('Added password_hash column to users table.');
    } else {
      console.log('password_hash column already exists.');
    }

    const appointmentColumns = [
      ['appointment_time', `ALTER TABLE appointments ADD COLUMN appointment_time VARCHAR(100) NOT NULL DEFAULT '' AFTER appointment_date`],
      ['reason', `ALTER TABLE appointments ADD COLUMN reason TEXT AFTER appointment_time`],
      ['status', `ALTER TABLE appointments ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'Confirmed' AFTER reason`]
    ];

    for (const [columnName, alterSql] of appointmentColumns) {
      const [matches] = await connection.query(`SHOW COLUMNS FROM appointments LIKE ?`, [columnName]);
      if (matches.length === 0) {
        await connection.query(alterSql);
        console.log(`Added ${columnName} column to appointments table.`);
      } else {
        console.log(`${columnName} column already exists.`);
      }
    }

    await connection.query(`
      CREATE TABLE IF NOT EXISTS feedback (
        id VARCHAR(36) PRIMARY KEY,
        user_id VARCHAR(36) NOT NULL,
        name VARCHAR(255),
        message TEXT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
      )
    `);
    console.log('feedback table is ready.');

    await connection.end();
  } catch (error) {
    console.error('Migration failed:', error);
  }
}
migrate();
