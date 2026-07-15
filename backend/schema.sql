CREATE DATABASE IF NOT EXISTS tmd_db;
USE tmd_db;

CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  auth_provider VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255),
  profile_image_path TEXT,
  height_cm FLOAT,
  weight_kg FLOAT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pain_records (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  date VARCHAR(50) NOT NULL,
  pain_level INT NOT NULL,
  stress_level INT NOT NULL,
  location VARCHAR(255) NOT NULL,
  type VARCHAR(255) DEFAULT 'Dull',
  timestamp BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sleep_records (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  date VARCHAR(50) NOT NULL,
  sleep_hours FLOAT NOT NULL,
  sleep_quality VARCHAR(50) NOT NULL,
  jaw_clenching BOOLEAN NOT NULL,
  morning_stiffness VARCHAR(50) NOT NULL,
  wakeup_feeling VARCHAR(50) NOT NULL,
  notes TEXT,
  timestamp BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS exercise_records (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  date VARCHAR(50) NOT NULL,
  exercise_name VARCHAR(255) NOT NULL,
  duration_sec INT NOT NULL,
  category VARCHAR(100) NOT NULL,
  timestamp BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS wellness_records (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  date VARCHAR(50) NOT NULL,
  sleep_quality VARCHAR(50) NOT NULL,
  jaw_stiffness VARCHAR(50) NOT NULL,
  teeth_grinding BOOLEAN NOT NULL,
  mood VARCHAR(50) NOT NULL,
  water_intake INT NOT NULL,
  energy_level INT NOT NULL,
  notes TEXT,
  timestamp BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS assessment_records (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  timestamp BIGINT NOT NULL,
  date VARCHAR(50) NOT NULL,
  q1_teeth_grinding BOOLEAN NOT NULL,
  q2_jaw_clenching BOOLEAN NOT NULL,
  q3_chew_gum BOOLEAN NOT NULL,
  q4_bite_nails BOOLEAN NOT NULL,
  q5_jaw_clicking BOOLEAN NOT NULL,
  q6_difficulty_chewing BOOLEAN NOT NULL,
  q7_morning_stiffness BOOLEAN NOT NULL,
  q8_frequent_headaches BOOLEAN NOT NULL,
  q9_sleep_less_than_6_hours BOOLEAN NOT NULL,
  q10_high_stress BOOLEAN NOT NULL,
  q11_poor_posture BOOLEAN NOT NULL,
  q12_one_side_chewing BOOLEAN NOT NULL,
  sleep_duration FLOAT NOT NULL,
  water_intake FLOAT NOT NULL,
  stress_frequency VARCHAR(50) NOT NULL,
  jaw_pain_frequency VARCHAR(50) NOT NULL,
  exercise_consistency VARCHAR(50) NOT NULL,
  smart_analysis TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reports (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  report_url TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointments (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  doctor_name VARCHAR(255) NOT NULL,
  appointment_date VARCHAR(100) NOT NULL,
  appointment_time VARCHAR(100) NOT NULL,
  reason TEXT,
  status VARCHAR(50) NOT NULL DEFAULT 'Confirmed',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS feedback (
  id VARCHAR(36) PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  name VARCHAR(255),
  message TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
