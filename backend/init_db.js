const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

async function initDB() {
  try {
    console.log('Connecting to MySQL server...');
    
    // Connect without a specific database to run CREATE DATABASE
    const connection = await mysql.createConnection({
      host: process.env.DB_HOST || 'localhost',
      user: process.env.DB_USER || 'root',
      password: process.env.DB_PASSWORD || '',
      multipleStatements: true
    });

    console.log('Connected successfully! Reading schema.sql...');
    const schemaSql = fs.readFileSync(path.join(__dirname, 'schema.sql'), 'utf8');

    console.log('Executing schema.sql...');
    await connection.query(schemaSql);
    
    console.log('Database and tables initialized perfectly!');
    await connection.end();
  } catch (error) {
    console.error('Failed to initialize database:', error);
  }
}

initDB();
