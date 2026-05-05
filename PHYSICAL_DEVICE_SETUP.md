# Physical Device Setup Guide

Run the Android app on a real Android device against the local FastAPI backend.

## Requirements

- Android device and development machine on the **same Wi-Fi network**
- Device has USB debugging or wireless debugging enabled

## Step-by-Step

### 1. Find your machine's IP address

**macOS / Linux**
```bash
ip route get 1 | awk '{print $7}'
# or
ifconfig | grep "inet " | grep -v 127.0.0.1
```

**Windows**
```cmd
ipconfig | findstr IPv4
```

Example result: `192.168.1.42`

### 2. Start the backend (bound to all interfaces)

```bash
cd backend
source .venv/bin/activate
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

### 3. Configure the Android app

In `Constants.kt`:
```kotlin
val ACTIVE_BACKEND_ENV: BackendEnvironment = BackendEnvironment.LOCAL_DEVICE
const val LOCAL_DEVICE_IP = "192.168.1.42"   // ← replace with your IP
```

### 4. Update Network Security Config (if IP differs)

Open `app/src/main/res/xml/network_security_config.xml` and add your machine's IP:
```xml
<domain includeSubdomains="false">192.168.1.42</domain>
```

### 5. Verify connectivity from the device

Open a browser on the device and navigate to `http://192.168.1.42:8000/health`.
You should see `{"status":"ok"}`.

### 6. Run the app

Build and run the app from Android Studio targeting the physical device.

## Troubleshooting

| Problem | Solution |
|---|---|
| `Connection refused` | Confirm both devices are on the same Wi-Fi; check firewall on your machine (allow port 8000) |
| `CLEARTEXT communication not permitted` | Add the IP to `network_security_config.xml` |
| Slow uploads | Compress images before upload (enabled by default via `IMAGE_COMPRESSION_QUALITY=80`) |
