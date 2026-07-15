const mysql = require('mysql2');
require('dotenv').config();

const pool = mysql.createPool({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'tmd_user',
  password: process.env.DB_PASSWORD || 'tmd_password',
  database: process.env.DB_NAME || 'tmd_db',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
  typeCast(field, next) {
    if (field.type === 'TINY' && field.length === 1) {
      return field.string() === '1';
    }

    return next();
  }
});

module.exports = pool.promise();
