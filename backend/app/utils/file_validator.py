"""File validation utilities."""

from __future__ import annotations

import imghdr
import logging
import os
from pathlib import Path

from app.core.config import settings
from app.core.constants import ALLOWED_EXTENSIONS, ALLOWED_MIME_TYPES
from app.core.exceptions import FileTooLargeError, UnsupportedFileTypeError

logger = logging.getLogger(__name__)

# Magic bytes for DICOM files
DICOM_MAGIC = b"DICM"
DICOM_MAGIC_OFFSET = 128


def validate_file_extension(filename: str) -> None:
    """Raise UnsupportedFileTypeError if the extension is not allowed."""
    suffix = Path(filename).suffix.lower()
    if suffix not in ALLOWED_EXTENSIONS:
        raise UnsupportedFileTypeError(
            detail=f"File extension '{suffix}' is not supported. Allowed: {ALLOWED_EXTENSIONS}"
        )


def validate_file_size(file_path: str) -> None:
    """Raise FileTooLargeError if the file exceeds the configured max size."""
    max_bytes = settings.MAX_FILE_SIZE_MB * 1024 * 1024
    size = os.path.getsize(file_path)
    if size > max_bytes:
        raise FileTooLargeError(
            detail=f"File size {size / 1024 / 1024:.1f} MB exceeds maximum {settings.MAX_FILE_SIZE_MB} MB"
        )


def is_dicom_file(file_path: str) -> bool:
    """Return True if the file has DICOM magic bytes."""
    try:
        with open(file_path, "rb") as fh:
            fh.seek(DICOM_MAGIC_OFFSET)
            magic = fh.read(4)
        return magic == DICOM_MAGIC
    except OSError:
        return False


def validate_file_content(file_path: str) -> str:
    """Validate file content and return detected modality string.

    Returns one of: 'dicom', 'png', 'jpg'
    Raises UnsupportedFileTypeError for unsupported content.
    """
    if is_dicom_file(file_path):
        return "dicom"

    img_type = imghdr.what(file_path)
    if img_type in ("png",):
        return "png"
    if img_type in ("jpeg",):
        return "jpg"

    # Accept based on extension alone for NIfTI etc.
    suffix = Path(file_path).suffix.lower()
    if suffix in (".nii", ".gz"):
        return "nifti"

    raise UnsupportedFileTypeError(
        detail=f"Could not identify supported file type for '{Path(file_path).name}'."
    )


def validate_upload(filename: str, file_path: str) -> str:
    """Run all validations and return detected content type."""
    validate_file_extension(filename)
    validate_file_size(file_path)
    return validate_file_content(file_path)
