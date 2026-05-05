# PDD Medical AI Platform – Backend

Production-grade FastAPI backend for **Inferior Alveolar Nerve (IAN) detection and segmentation** from DICOM/CBCT/panoramic dental images, fully integrated with the Firebase-powered Android application.

---

## Architecture

```
Android App (Kotlin + Firebase)
    ↓  Firebase Auth token
Firebase Storage  ──────────────→  FastAPI Backend (Python 3.11+)
Firebase Firestore ←────────────   AI Inference Pipeline (PyTorch)
    ↓  Realtime listener             ↓
Android Result Download          Result Generation + Storage
```

---

## Directory Structure

```
backend/
├── app/
│   ├── main.py                  # FastAPI application factory + lifespan
│   ├── api/v1/                  # REST endpoints
│   │   ├── auth.py              # GET /api/v1/auth/me
│   │   ├── processing.py        # POST/GET /api/v1/jobs/*
│   │   ├── admin.py             # GET /api/v1/admin/metrics
│   │   └── health.py            # GET /health
│   ├── core/
│   │   ├── config.py            # Pydantic v2 settings
│   │   ├── firebase_admin.py    # Firebase Admin SDK singleton
│   │   ├── security.py          # JWT verification
│   │   ├── exceptions.py        # Exception hierarchy
│   │   └── constants.py         # App-wide constants
│   ├── services/
│   │   ├── firebase_service.py  # High-level Firestore operations
│   │   ├── storage_service.py   # Firebase Storage upload/download
│   │   ├── job_service.py       # Job orchestration
│   │   ├── ai_inference_service.py  # Full inference pipeline
│   │   └── result_generator_service.py  # Upload artefacts
│   ├── repositories/            # Firestore CRUD (jobs, users, models)
│   ├── models/                  # Domain models (Job, User, ProcessingLog)
│   ├── schemas/                 # Pydantic request/response schemas
│   ├── ai/
│   │   ├── architectures/       # U-Net, Attention U-Net, DeepLabV3+
│   │   ├── preprocessing/       # DICOM loader, image processor, normalizer
│   │   ├── inference/           # Model loader, inferencer, GPU utils
│   │   ├── postprocessing/      # Mask, overlay, heatmap, report
│   │   ├── datasets/            # PyTorch dataset
│   │   ├── training/            # Trainer, loss functions, metrics, callbacks
│   │   ├── metrics/             # Dice, IoU, Hausdorff
│   │   └── checkpoints/         # Model versioning
│   ├── workers/                 # Async job worker + queue manager
│   ├── middleware/              # Auth, rate-limit, logging middleware
│   ├── utils/                   # File validator, temp manager, metrics
│   └── exports/                 # ONNX export
├── tests/                       # Unit + integration tests
├── scripts/                     # Training, export, setup scripts
├── Dockerfile                   # CPU production image
├── Dockerfile.gpu               # CUDA-enabled image
├── docker-compose.yml           # Local/VM deployment
├── docker-compose.gpu.yml       # GPU deployment
├── nginx.conf                   # Nginx reverse proxy
├── requirements.txt
├── requirements-dev.txt
└── .env.example
```

---

## Quick Start

### Prerequisites

- Python 3.11+
- Firebase project with Firestore, Storage, and Auth enabled
- Service account JSON key (download from Firebase Console → Project Settings → Service Accounts)

### Local Development

```bash
# 1. Create and activate virtual environment
python -m venv .venv
source .venv/bin/activate  # Windows: .venv\Scripts\activate

# 2. Install dependencies
pip install -r requirements.txt
# For development:
pip install -r requirements-dev.txt

# 3. Configure environment
cp .env.example .env
# Edit .env and set GOOGLE_APPLICATION_CREDENTIALS, FIREBASE_PROJECT_ID, etc.

# 4. Run the server
uvicorn app.main:app --reload --port 8000

# 5. Open API docs
open http://localhost:8000/docs
```

### Docker Compose (CPU)

```bash
# Place service-account.json in backend/secrets/
mkdir -p secrets
cp /path/to/your-service-account.json secrets/service-account.json

# Start
docker-compose up -d

# View logs
docker-compose logs -f api
```

### Docker Compose (GPU)

```bash
# Requires: nvidia-container-toolkit installed on the host
docker-compose -f docker-compose.gpu.yml up -d
```

---

## Environment Variables

See [`.env.example`](.env.example) for all options. Key variables:

| Variable | Description | Default |
|---|---|---|
| `FIREBASE_PROJECT_ID` | Firebase project ID | `medical-processor-prod` |
| `FIREBASE_STORAGE_BUCKET` | Storage bucket name | `medical-processor-prod.appspot.com` |
| `GOOGLE_APPLICATION_CREDENTIALS` | Path to service-account JSON | — |
| `MODEL_DEVICE` | `auto` \| `cpu` \| `cuda` | `auto` |
| `WORKER_CONCURRENCY` | Number of parallel job workers | `4` |
| `LOG_LEVEL` | `DEBUG` \| `INFO` \| `WARNING` | `INFO` |
| `RATE_LIMIT_PER_MINUTE` | API requests per user per minute | `60` |

---

## API Endpoints

### Public

| Method | Path | Description |
|---|---|---|
| `GET` | `/health` | Health check |
| `GET` | `/docs` | Swagger UI |

### Authenticated (Firebase JWT required)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/auth/me` | Current user info |
| `POST` | `/api/v1/jobs` | Create processing job |
| `GET` | `/api/v1/jobs` | List user's jobs |
| `GET` | `/api/v1/jobs/{id}` | Get job status |
| `POST` | `/api/v1/jobs/{id}/cancel` | Cancel job |
| `POST` | `/api/v1/jobs/{id}/retry` | Retry failed job |
| `GET` | `/api/v1/admin/metrics` | System metrics |

### Authentication

All protected endpoints require:
```
Authorization: Bearer <firebase-id-token>
```

---

## Android Integration

### Upload Flow

```kotlin
// 1. Upload file to Firebase Storage
val storageRef = Firebase.storage.reference.child("uploads/$uid/scan.dcm")
storageRef.putFile(fileUri).await()

// 2. Create processing job via backend API
val response = apiService.createJob(CreateJobRequest(
    storage_path = "uploads/$uid/scan.dcm",
    modality = "panoramic"
))
val jobId = response.job_id

// 3. Listen to Firestore for real-time status updates
Firebase.firestore.collection("jobs").document(jobId)
    .addSnapshotListener { snapshot, _ ->
        val status = snapshot?.getString("status")
        val resultUrls = snapshot?.get("result_urls") as? Map<String, String>
        // Update UI
    }
```

### Result Download

```kotlin
// When status == "completed", download overlay from result_urls.overlay_url
val overlayUrl = resultUrls["overlay_url"]
Glide.with(this).load(overlayUrl).into(imageView)
```

---

## AI Models

| Architecture | GPU Memory | Dice Target | Use Case |
|---|---|---|---|
| U-Net | ~2 GB | ≥ 0.85 | Fast baseline, limited data |
| Attention U-Net | ~3 GB | ≥ 0.87 | Improved boundary accuracy |
| DeepLabV3+ | ~4 GB | ≥ 0.86 | Multi-scale context |

### Training

```bash
python scripts/train_model.py \
  --arch unet \
  --image-dir /data/images \
  --mask-dir /data/masks \
  --epochs 100 \
  --batch-size 4
```

### ONNX Export

```bash
python scripts/export_onnx.py \
  --arch unet \
  --checkpoint /app/checkpoints/unet_best.pth \
  --output /app/checkpoints/unet.onnx
```

---

## Firestore Schema

### `jobs` collection

```json
{
  "job_id": "uuid",
  "user_id": "firebase-uid",
  "storage_path": "uploads/<uid>/<filename>",
  "modality": "panoramic",
  "model_architecture": "unet",
  "status": "pending|uploading|processing|completed|failed|cancelled",
  "progress": 0,
  "retry_count": 0,
  "error_message": null,
  "result_urls": {
    "mask_url": "https://...",
    "overlay_url": "https://...",
    "heatmap_url": "https://...",
    "report_url": "https://..."
  },
  "created_at": "ISO8601",
  "updated_at": "ISO8601"
}
```

### Recommended Indexes

- `jobs`: `(user_id ASC, created_at DESC)`
- `jobs`: `(status ASC, created_at ASC)`

Create via: Firebase Console → Firestore → Indexes

---

## Testing

```bash
# Run all tests
pytest tests/ -v

# With coverage
pytest tests/ --cov=app --cov-report=html

# Specific test module
pytest tests/test_ai/ -v
```

---

## Deployment Guide

### Single VM (Docker Compose)

1. Provision Ubuntu 22.04 VM (4+ vCPU, 16+ GB RAM)
2. Install Docker + Docker Compose
3. Clone repo, configure `.env` and `secrets/service-account.json`
4. `docker-compose up -d`
5. Configure DNS + SSL (see `nginx.conf` for HTTPS template)

### GPU VM

1. Provision VM with NVIDIA GPU (T4, A10G, etc.)
2. Install `nvidia-container-toolkit`
3. `docker-compose -f docker-compose.gpu.yml up -d`

### Kubernetes

- Use `Deployment` with `resources.limits.nvidia.com/gpu: 1` for GPU pods
- Mount service-account JSON via `Secret`
- Use `HorizontalPodAutoscaler` for scaling

### Cloud Run + GPU (Preview)

- Build GPU image, push to Artifact Registry
- Deploy to Cloud Run with GPU accelerator (beta)
- Set `GOOGLE_APPLICATION_CREDENTIALS` via Secret Manager

---

## Security Checklist

- [x] Firebase JWT verified on every protected request
- [x] Rate limiting per user (60 req/min default)
- [x] File size validation (500 MB limit)
- [x] File type/content validation (MIME, magic bytes)
- [x] Structured exception sanitisation (no stack traces to clients)
- [x] Non-root Docker user
- [x] CORS configuration
- [x] Secure temp file cleanup
- [ ] HTTPS / TLS termination (configure nginx.conf for production)
- [ ] Service account with minimal IAM permissions
- [ ] PHI encryption at rest (enable Firebase Storage encryption)
- [ ] Audit logging for PHI access
- [ ] Compliance review (HIPAA/GDPR as applicable)

---

## Performance Targets

| Metric | Target |
|---|---|
| Dice coefficient | ≥ 0.85 |
| IoU | ≥ 0.75 |
| Processing time (GPU) | 2–5 s/image |
| Processing time (CPU) | 10–20 s/image |
| API response (job create) | < 200 ms |
| Max file size | 500 MB |

---

## Hardware Recommendations

| Deployment | Recommendation |
|---|---|
| Development | Any modern CPU, 8 GB RAM |
| Production (CPU) | 8+ vCPU, 32 GB RAM |
| Production (GPU) | NVIDIA T4 (16 GB VRAM) or A10G |
| Storage | 100 GB+ SSD for temp + checkpoints |

---

## Estimated Cloud Cost (reference)

| Setup | Approx. Monthly |
|---|---|
| Single CPU VM (e8-standard-8) | $150–250 |
| GPU VM (T4, g2-standard-8) | $400–600 |
| Firebase Spark (free tier) | $0–50 |
| Firebase Blaze (pay-as-you-go) | Varies by usage |

*Estimates for Google Cloud. Adjust for AWS/Azure.*
