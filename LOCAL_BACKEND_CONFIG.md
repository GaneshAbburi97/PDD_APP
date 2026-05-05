# Local Backend Configuration

How to configure the Android app to reach the FastAPI backend in different local environments.

## Environment Selection

Edit `Constants.ACTIVE_BACKEND_ENV` in
`app/src/main/kotlin/com/medical/fileprocessor/util/Constants.kt`:

```kotlin
val ACTIVE_BACKEND_ENV: BackendEnvironment = BackendEnvironment.EMULATOR
```

| Value | When to use | Backend URL |
|---|---|---|
| `EMULATOR` | Running on Android emulator (AVD) | `http://10.0.2.2:8000/` |
| `LOCAL_DEVICE` | Physical Android device on same Wi-Fi | `http://<LAN-IP>:8000/` |
| `CLOUD` | Production / staging | Firebase Cloud Functions URL |

## Physical Device IP

When using `LOCAL_DEVICE`, also update:

```kotlin
const val LOCAL_DEVICE_IP = "192.168.1.100"  // ← your machine's LAN IP
```

Find your machine's IP:
- **macOS/Linux**: `ip route get 1 | awk '{print $7}'` or `ifconfig | grep "inet "`
- **Windows**: `ipconfig | findstr IPv4`

## Network Security

Cleartext HTTP is allowed only for the listed local hosts
(`10.0.2.2`, `192.168.1.100`, `localhost`, `127.0.0.1`) via
`app/src/main/res/xml/network_security_config.xml`.

To add another LAN address, add a `<domain>` entry there.

## Backend Health Check

The Android app can call `GET /health` before submitting a job.
If the response is not `{"status":"ok"}`, the user sees an offline banner
and is prompted to retry once the backend is reachable.
