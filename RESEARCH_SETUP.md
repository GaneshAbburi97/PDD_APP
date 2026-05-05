# Research Setup Guide

Complete guide for running the Medical AI Platform in a local research environment.

## Prerequisites

| Tool | Version | Notes |
|---|---|---|
| Android Studio | Flamingo+ | For building the Android app |
| Python | 3.11+ | For the FastAPI backend |
| Docker (optional) | 20+ | For containerised deployment |
| Firebase account | — | Free tier is sufficient |

---

## 1. Firebase Setup (one-time)

1. Go to [Firebase Console](https://console.firebase.google.com) and create a project.
2. Enable **Authentication** (Email/Password provider).
3. Enable **Cloud Firestore** (production mode, then add rules).
4. Enable **Firebase Storage**.
5. Download `google-services.json` and place it in `app/`.
6. Download a **Service Account** key (JSON) and save it as `backend/firebase-credentials.json`.

### Firestore Security Rules

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /jobs/{jobId} {
      allow read, write: if request.auth != null && request.auth.uid == resource.data.userId;
    }
  }
}
```

---

## 2. Backend Setup

```bash
cd backend
cp .env.example .env   # then edit FIREBASE_CREDENTIALS_PATH etc.
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

Verify: `curl http://localhost:8000/health` should return `{"status":"ok"}`.

---

## 3. Android App Setup

1. Open the project in Android Studio.
2. Open `app/src/main/kotlin/.../util/Constants.kt`.
3. Set `ACTIVE_BACKEND_ENV`:
   - `BackendEnvironment.EMULATOR` — running on AVD
   - `BackendEnvironment.LOCAL_DEVICE` — physical device (also update `LOCAL_DEVICE_IP`)
4. Run the app.

---

## 4. End-to-End Test

1. Start the backend (`uvicorn ...`).
2. Launch the Android app on the emulator.
3. Log in with a Firebase test account.
4. Upload a DICOM / PNG file.
5. Watch the processing screen — the job should move from QUEUED → PROCESSING → COMPLETED.
6. Download and view the result.

See `TESTING_CHECKLIST.md` for a detailed checklist.
