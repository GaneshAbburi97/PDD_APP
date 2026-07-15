# TMD Care AI Appium E2E Tests

This folder contains the Appium end-to-end testing framework for the Android mobile app.

## What It Tests

- Login, signup, and forgot-password screens
- Authenticated dashboard and bottom navigation
- Pain map save flow
- Daily wellness save flow
- Sleep tracking save flow
- Exercise program screen
- Progress tabs and health report screen
- Profile, doctor list, and appointment booking screen reachability

Every pytest run generates an Excel analysis report in:

```text
appium_tests/reports/
```

The workbook includes:

- Summary pass/fail/skip counts
- Detailed per-test results
- Coverage matrix by app area
- Failure or skip details
- Screenshot links for failed tests
- Recommendations for the next run

## Prerequisites

1. Install Python 3.10+.
2. Install Node.js.
3. Install Appium 2:

```powershell
npm install -g appium
appium driver install uiautomator2
```

4. Connect an Android phone or start an emulator:

```powershell
adb devices
```

5. Install the TMD app on the device, or pass an APK path when running the tests.

## Install Python Dependencies

From this folder:

```powershell
cd D:\projects\AndroidStudioProjects\TMDApp2\appium_tests
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

## Start Appium

Keep this running in a separate terminal:

```powershell
appium
```

## Test Credentials

Authenticated E2E tests need a real test account that can log into the backend.

```powershell
$env:TMD_TEST_EMAIL = "test@example.com"
$env:TMD_TEST_PASSWORD = "your-password"
```

If these are not set, the authenticated tests are skipped and still appear in the Excel report.

## Run All Tests

```powershell
cd D:\projects\AndroidStudioProjects\TMDApp2\appium_tests
pytest -v
```

Or use the helper script:

```powershell
.\scripts\run_appium_tests.ps1
```

## Run With APK Path

```powershell
.\scripts\run_appium_tests.ps1 -ApkPath "D:\projects\AndroidStudioProjects\TMDApp2\app\build\outputs\apk\debug\app-debug.apk"
```

## Run Only Selected Areas

```powershell
pytest -v -m auth
pytest -v -m navigation
pytest -v -m tracking
pytest -v -m reports
pytest -v -m support
```

## Useful Configuration

You can set these environment variables before running:

```powershell
$env:APPIUM_SERVER_URL = "http://127.0.0.1:4723"
$env:APPIUM_DEVICE_NAME = "Android"
$env:APPIUM_UDID = "your-device-id"
$env:TMD_APP_PACKAGE = "com.example.tmdapp"
$env:TMD_APP_ACTIVITY = ".MainActivity"
$env:TMD_APK_PATH = "D:\path\to\app-debug.apk"
$env:APPIUM_NO_RESET = "false"
```

## Output Files

After the run, open the newest workbook:

```text
appium_tests/reports/tmd_appium_e2e_report_YYYYMMDD_HHMMSS.xlsx
```

Raw JSON and screenshots are saved beside it for traceability.
