# Emulator Setup Guide

Run the Android app on the Android Emulator (AVD) against the local FastAPI backend.

## Step-by-Step

### 1. Start the backend

```bash
cd backend
source .venv/bin/activate
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

### 2. Configure the Android app

In `Constants.kt`:
```kotlin
val ACTIVE_BACKEND_ENV: BackendEnvironment = BackendEnvironment.EMULATOR
```

The emulator routes `10.0.2.2` to the host machine's loopback address,
so the app will reach `http://10.0.2.2:8000/`.

### 3. Verify connectivity

From within the emulator browser (or adb shell):
```
curl http://10.0.2.2:8000/health
```
Expected: `{"status":"ok","version":"1.0.0","model_loaded":false}`

### 4. Run the app

Build and run the app from Android Studio targeting the emulator.

## Troubleshooting

| Problem | Solution |
|---|---|
| `Connection refused` on `10.0.2.2` | Make sure the backend is running and bound to `0.0.0.0` not `127.0.0.1` |
| `CLEARTEXT communication not permitted` | Check that `network_security_config.xml` lists `10.0.2.2` |
| Firebase auth fails | Ensure `google-services.json` is present in `app/` |
