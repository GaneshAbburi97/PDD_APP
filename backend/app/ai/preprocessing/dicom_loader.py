"""DICOM / image file loader with automatic format detection."""

from __future__ import annotations

import logging
from pathlib import Path
from typing import Optional, Tuple

import numpy as np

logger = logging.getLogger(__name__)


def load_image(file_path: str) -> Tuple[np.ndarray, dict]:
    """Load a medical image from file and return (array, metadata).

    Supports: DICOM, PNG, JPG, NIfTI.
    Returns a float32 numpy array normalised to [0, 1] and a metadata dict.
    """
    path = Path(file_path)
    suffix = path.suffix.lower()

    if _is_dicom(file_path):
        return _load_dicom(file_path)

    if suffix in (".nii", ".gz"):
        return _load_nifti(file_path)

    # Default: PIL image
    return _load_standard_image(file_path)


def _is_dicom(file_path: str) -> bool:
    try:
        with open(file_path, "rb") as fh:
            fh.seek(128)
            return fh.read(4) == b"DICM"
    except OSError:
        return False


def _load_dicom(file_path: str) -> Tuple[np.ndarray, dict]:
    import pydicom  # type: ignore

    ds = pydicom.dcmread(file_path)
    pixel_array = ds.pixel_array.astype(np.float32)

    # Apply rescale slope/intercept if present
    slope = getattr(ds, "RescaleSlope", 1.0)
    intercept = getattr(ds, "RescaleIntercept", 0.0)
    pixel_array = pixel_array * float(slope) + float(intercept)

    # Normalise to [0, 1]
    pmin, pmax = pixel_array.min(), pixel_array.max()
    if pmax > pmin:
        pixel_array = (pixel_array - pmin) / (pmax - pmin)

    metadata = {
        "modality": getattr(ds, "Modality", "UNKNOWN"),
        "rows": int(getattr(ds, "Rows", pixel_array.shape[-2])),
        "columns": int(getattr(ds, "Columns", pixel_array.shape[-1])),
        "patient_id": str(getattr(ds, "PatientID", "")),
        "study_date": str(getattr(ds, "StudyDate", "")),
        "bits_allocated": int(getattr(ds, "BitsAllocated", 16)),
        "source": "dicom",
    }
    logger.debug("Loaded DICOM: shape=%s modality=%s", pixel_array.shape, metadata["modality"])
    return pixel_array, metadata


def _load_nifti(file_path: str) -> Tuple[np.ndarray, dict]:
    try:
        import nibabel as nib  # type: ignore
        img = nib.load(file_path)
        data = np.asarray(img.dataobj, dtype=np.float32)
        pmin, pmax = data.min(), data.max()
        if pmax > pmin:
            data = (data - pmin) / (pmax - pmin)
        metadata = {"source": "nifti", "affine": img.affine.tolist()}
        return data, metadata
    except ImportError:
        logger.warning("nibabel not installed; falling back to PIL for %s", file_path)
        return _load_standard_image(file_path)


def _load_standard_image(file_path: str) -> Tuple[np.ndarray, dict]:
    from PIL import Image  # type: ignore

    img = Image.open(file_path).convert("RGB")
    arr = np.array(img, dtype=np.float32) / 255.0
    metadata = {
        "source": "pil",
        "size": list(img.size),
        "mode": img.mode,
    }
    logger.debug("Loaded image via PIL: shape=%s", arr.shape)
    return arr, metadata
