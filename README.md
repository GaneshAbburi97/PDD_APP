# PDD_APP: Research-Level Medical File Processor

PDD_APP is a high-grade, traceable, and reproducible full-stack medical image processing platform. It integrates a Kotlin Android client with a FastAPI backend featuring a lightweight PyTorch U-Net segmentation neural network.

---

## 🔬 Research-Grade Lineage & Traceability Spec

For every image processed, a deterministic job execution environment is established on the backend at `backend/data/jobs/{job_id}/` containing:
1. `input/`: The uploaded raw `.nii` / `.nii.gz` file.
2. `output/`: The generated segmentation mask file (`{job_id}_result.nii.gz`).
3. `report.json`: A comprehensive data lineage and reproducibility report.

### Example `report.json` Specification:
```json
{
  "jobId": "fb4d9e03-822e-42e3-8b20-e18d10fd881f",
  "status": "COMPLETED",
  "createdAt": 1716912345000,
  "updatedAt": 1716912365000,
  "metrics": {
    "volume_ml": "130.882",
    "segmentation_score": "0.89",
    "processing_unit": "CPU",
    "timestamp": "1716912365000",
    "input_hash": "c09265ea4e85741f0f37c35f29e1eb1bb5d2cf59fb87fbc2a5efbc1b293d3d65",
    "model_version": "unet_v1.0.0",
    "inference_time_ms": "2050",
    "commit_sha": "6d3b4a2c918f0a4a83e0c0c1bcfdf12e0f8de0b1",
    "device": "cpu"
  }
}
```

---

## ⚡ Setup & Local Execution Guide

### 1. Backend Server Setup (Windows Local)
Ensure Python 3.11 is active, and run in PowerShell:
```powershell
# Navigate to the backend folder
cd C:\Users\prasa\Documents\PDD_APP\backend

# Install/Update standard dependencies
..\venv\Scripts\python -m pip install -r requirements.txt

# Start the FastAPI server locally
..\venv\Scripts\python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

*Note: If no Firebase service credentials are found, the backend gracefully switches to local persistence fallback (`backend/data/local_jobs_db.json`), requiring zero external network databases.*

### 2. Android Studio Setup
1. Open the project `C:\Users\prasa\Documents\PDD_APP` in Android Studio.
2. Select the `debug` build variant. It automatically resolves the API base URL to `http://10.0.2.2:8000` (Emulator bridge) with local cleartext permitted.
3. Build and launch on your Android Emulator.

---

## 🧪 Automated Testing & CI Checks

Ensure robust platform behavior by executing the complete test suite.

### Running Backend Tests (FastAPI Pytest)
```powershell
cd C:\Users\prasa\Documents\PDD_APP\backend
..\venv\Scripts\python -m pip install pytest httpx
..\venv\Scripts\python -m pytest
```

### Running Android Client Tests (JUnit + MockK)
```powershell
cd C:\Users\prasa\Documents\PDD_APP
.\gradlew testDebugUnitTest
```

---

## 📂 DevOps Containerization

Deploy the microservices cleanly using Docker:
```bash
cd C:\Users\prasa\Documents\PDD_APP\backend
docker-compose up --build -d
```
