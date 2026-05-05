"""Application-wide constants."""

from enum import Enum


class JobStatus(str, Enum):
    PENDING = "pending"
    UPLOADING = "uploading"
    PROCESSING = "processing"
    COMPLETED = "completed"
    FAILED = "failed"
    CANCELLED = "cancelled"


class ModelArchitecture(str, Enum):
    UNET = "unet"
    ATTENTION_UNET = "attention_unet"
    DEEPLAB = "deeplab"


class ImageModality(str, Enum):
    PANORAMIC = "panoramic"
    CBCT = "cbct"
    PNG = "png"
    JPG = "jpg"
    DICOM = "dicom"


# Supported file MIME types
ALLOWED_MIME_TYPES = {
    "image/jpeg",
    "image/png",
    "application/dicom",
    "application/octet-stream",  # .dcm files often come as this
}

# Supported file extensions
ALLOWED_EXTENSIONS = {".dcm", ".dicom", ".png", ".jpg", ".jpeg", ".nii", ".nii.gz"}

# Max upload size: 500 MB
MAX_FILE_SIZE_BYTES = 500 * 1024 * 1024

# Firestore collection names
COLLECTION_USERS = "users"
COLLECTION_JOBS = "jobs"
COLLECTION_PROCESSING_LOGS = "processing_logs"
COLLECTION_DOWNLOADS = "downloads"
COLLECTION_MODEL_VERSIONS = "model_versions"

# Firebase Storage paths
STORAGE_UPLOADS_PREFIX = "uploads"
STORAGE_RESULTS_PREFIX = "results"

# Job processing timeouts
JOB_PROCESSING_TIMEOUT_SECONDS = 300  # 5 minutes
JOB_MAX_RETRIES = 3
JOB_RETRY_BACKOFF_BASE = 2  # exponential backoff base in seconds

# Result output types
RESULT_MASK_FILENAME = "segmentation_mask.png"
RESULT_OVERLAY_FILENAME = "overlay.png"
RESULT_HEATMAP_FILENAME = "confidence_heatmap.png"
RESULT_REPORT_FILENAME = "report.json"

# Inference image size
INFERENCE_IMAGE_SIZE = (512, 512)

# Default model architecture
DEFAULT_MODEL_ARCHITECTURE = ModelArchitecture.UNET
