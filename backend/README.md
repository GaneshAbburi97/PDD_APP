# Medical AI Backend

Lightweight FastAPI backend for local research execution of the IAN segmentation pipeline.

## Quick Start

### 1. Install dependencies

```bash
cd backend
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
```

### 2. Configure environment

```bash
cp .env.example .env
# Edit .env — set FIREBASE_CREDENTIALS_PATH and FIREBASE_STORAGE_BUCKET
```

### 3. Run directly

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

The API is available at **http://localhost:8000**.
Interactive docs: **http://localhost:8000/docs** (only when `DEBUG=true`).

---

### Docker (optional)

```bash
docker compose up --build
```

---

## Android Integration

| Environment | Backend URL |
|---|---|
| Emulator (AVD) | `http://10.0.2.2:8000/` |
| Physical device (same LAN) | `http://<your-machine-IP>:8000/` |

Update `Constants.ACTIVE_BACKEND_ENV` in the Android app to switch environments.

---

## Key Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/health` | Health check — returns `{"status":"ok"}` |
| POST | `/api/v1/auth/verify` | Verify Firebase ID token |
| POST | `/api/v1/process` | Create + queue a processing job |
| GET | `/api/v1/process/status/{id}` | Poll job status |
| GET | `/api/v1/process/result/{id}` | Fetch completed result |
| POST | `/api/v1/process/cancel/{id}` | Cancel a job |
| POST | `/api/v1/process/retry/{id}` | Retry a failed job |

---

## Model Checkpoint

Place your trained `ian_unet.pth` file in `backend/models/`.
If no checkpoint is found the server will still start and run inference with random weights (useful for development).

Expected checkpoint format:
```python
torch.save({"model_state_dict": model.state_dict()}, "ian_unet.pth")
```

---

## CPU Performance

| Image size | Approximate inference time |
|---|---|
| 512 × 512 | 5–15 s |
| 256 × 256 | 1–3 s |

GPU inference is supported automatically when `INFERENCE_DEVICE=cuda` and a CUDA-capable GPU is available.
