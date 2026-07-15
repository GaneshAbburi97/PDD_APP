# TMD Care AI

TMD Care AI is a comprehensive health tracking and management application designed specifically for patients dealing with Temporomandibular Joint Disorders (TMD) and jaw pain.

## Features
- **Pain & Stress Mapping:** Log daily pain intensity, stress levels, and specific jaw/neck pain locations.
- **Wellness & Sleep Tracking:** Record sleep quality, water intake, mood, and behaviors like teeth grinding.
- **Guided Exercises:** Access a library of guided stretches and jaw relaxation exercises with video instructions.
- **AI Assistant:** Integrated chat with an AI assistant for quick TMD-related advice and support.
- **Doctor Appointments:** Search for specialists and book appointments directly through the app.
- **Automated Reporting:** Generates detailed Excel reports for tests and patient progress using automated CI/CD workflows.

## Technology Stack
- **Frontend:** Android (Kotlin, Jetpack Compose, Material 3, ExoPlayer for media).
- **Backend:** Node.js (Express.js, JWT authentication, Google Auth Library).
- **Database:** MySQL (managed locally or via Docker Compose).
- **Testing:** Appium (Python) for automated E2E testing and mock report generation.
- **CI/CD:** GitHub Actions (automatically runs Appium reports and publishes them as artifacts).

## Getting Started

### Prerequisites
- Android Studio
- Node.js (v16+)
- MySQL

### Running the Backend
1. Navigate to the `backend/` directory.
2. Run `npm install` to install dependencies.
3. Configure the `.env` file with your database credentials.
4. Run `npm run init-db` to initialize the database schema.
5. Run `npm run migrate` to apply any pending schema migrations.
6. Run `npm start` to launch the server on port 5000.

### Running the Android App
1. Open the project in Android Studio.
2. Ensure your device or emulator is connected.
3. Run `adb reverse tcp:5000 tcp:5000` to forward localhost traffic from the device to the backend.
4. Build and run the `app` module.

### Automated Test Reports
This repository uses GitHub Actions. Whenever code is pushed to the `main` branch, the `test_reports.yml` workflow automatically runs the Python test script in `appium_tests/` and uploads the resulting Excel files as workflow artifacts.
