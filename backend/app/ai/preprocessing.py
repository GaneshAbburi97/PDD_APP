"""backend/app/ai/preprocessing.py — Lightweight image preprocessing for CPU inference."""

from __future__ import annotations

import logging
from pathlib import Path
from typing import Tuple

import numpy as np

logger = logging.getLogger(__name__)

# Supported extensions
_IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg"}
_DICOM_EXTENSIONS = {".dcm", ".dicom"}


def load_and_preprocess(file_path: str, target_size: Tuple[int, int] = (512, 512)) -> np.ndarray:
    """
    Load an image or DICOM file and return a normalised float32 tensor
    of shape (1, 1, H, W) ready for PyTorch inference.

    Supported formats:
    - PNG / JPG (converted to grayscale)
    - DICOM (pixel_array extracted and normalised)
    - NIfTI is not handled here; use a dedicated loader if needed.

    Args:
        file_path: Absolute path to the input file.
        target_size: (height, width) to resize the image to.

    Returns:
        NumPy float32 array with shape (1, 1, H, W), values in [0, 1].
    """
    path = Path(file_path)
    suffix = path.suffix.lower()

    if suffix in _DICOM_EXTENSIONS:
        array = _load_dicom(path)
    elif suffix in _IMAGE_EXTENSIONS:
        array = _load_image(path)
    else:
        # Treat as raw image via PIL fallback
        array = _load_image(path)

    # Resize to target dimensions
    array = _resize(array, target_size)

    # Normalise to [0, 1]
    array = _normalise(array)

    # Add batch and channel dimensions: (H, W) → (1, 1, H, W)
    tensor = array[np.newaxis, np.newaxis, :, :].astype(np.float32)
    return tensor


# ── Loaders ──────────────────────────────────────────────────────────────────

def _load_dicom(path: Path) -> np.ndarray:
    try:
        import pydicom  # type: ignore
        ds = pydicom.dcmread(str(path))
        array = ds.pixel_array.astype(np.float32)
        logger.debug("Loaded DICOM: %s  shape=%s", path.name, array.shape)
        # Flatten 3-D volumes to middle slice
        if array.ndim == 3:
            array = array[array.shape[0] // 2]
        return array
    except Exception as exc:
        raise ValueError(f"Failed to load DICOM file {path}: {exc}") from exc


def _load_image(path: Path) -> np.ndarray:
    try:
        from PIL import Image  # type: ignore
        img = Image.open(str(path)).convert("L")  # Grayscale
        array = np.array(img, dtype=np.float32)
        logger.debug("Loaded image: %s  shape=%s", path.name, array.shape)
        return array
    except Exception as exc:
        raise ValueError(f"Failed to load image {path}: {exc}") from exc


# ── Transforms ───────────────────────────────────────────────────────────────

def _resize(array: np.ndarray, target_size: Tuple[int, int]) -> np.ndarray:
    import cv2  # type: ignore
    h, w = target_size
    return cv2.resize(array, (w, h), interpolation=cv2.INTER_LINEAR)


def _normalise(array: np.ndarray) -> np.ndarray:
    min_val = array.min()
    max_val = array.max()
    if max_val - min_val < 1e-6:
        return np.zeros_like(array, dtype=np.float32)
    return (array - min_val) / (max_val - min_val)
