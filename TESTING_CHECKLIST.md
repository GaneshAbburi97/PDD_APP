# End-to-End Testing Checklist

Use this checklist to verify the full pipeline before a research session.

## Backend

- [ ] Backend starts without errors: `uvicorn app.main:app --host 0.0.0.0 --port 8000`
- [ ] Health check passes: `curl http://localhost:8000/health` → `{"status":"ok"}`
- [ ] Firebase initialises (check logs for "Firebase Admin SDK initialised ✓")
- [ ] Docs page accessible in debug mode: `http://localhost:8000/docs`

## Android App — Authentication

- [ ] App launches without crash
- [ ] Login with valid Firebase account succeeds
- [ ] Invalid credentials show an error message
- [ ] Logout clears session

## Android App — Connectivity

- [ ] Correct `ACTIVE_BACKEND_ENV` is set in `Constants.kt`
- [ ] (Emulator) `10.0.2.2:8000/health` reachable
- [ ] (Physical device) `<LAN-IP>:8000/health` reachable
- [ ] Offline banner appears when backend is stopped
- [ ] Offline banner disappears when backend restarts

## Android App — File Upload & Processing

- [ ] File picker opens and allows selection of a DICOM / PNG / JPG file
- [ ] Upload progress bar shows incremental progress
- [ ] Processing screen shows animated progress bar
- [ ] Job status transitions: `QUEUED → PROCESSING → COMPLETED`
- [ ] Retry button works when backend returns an error
- [ ] Result screen shows segmentation details after completion

## Backend — Job Lifecycle

- [ ] `POST /api/v1/process` creates a Firestore job record
- [ ] Firestore `status` field updates: `QUEUED → PROCESSING → COMPLETED`
- [ ] `GET /api/v1/process/status/{id}` returns current progress
- [ ] `GET /api/v1/process/result/{id}` returns result after completion
- [ ] `POST /api/v1/process/cancel/{id}` cancels a queued job
- [ ] `POST /api/v1/process/retry/{id}` re-queues a failed job

## Firebase Free-Tier

- [ ] Firestore read/write count is reasonable (check Firebase console)
- [ ] Uploaded images use compression (≤80% JPEG quality, ≤512 px)
- [ ] Temp files are cleaned up after inference
