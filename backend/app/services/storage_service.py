"""Firebase Storage service: upload / download / delete helpers."""

from __future__ import annotations

import logging
import os
import tempfile
from pathlib import Path
from typing import Optional

from app.core.firebase_admin import get_storage_bucket
from app.core.exceptions import FileDownloadError, FileUploadError

logger = logging.getLogger(__name__)


class StorageService:
    """Wraps Firebase Storage operations."""

    def get_bucket(self):
        return get_storage_bucket()

    async def download_to_temp(self, storage_path: str, suffix: Optional[str] = None) -> str:
        """Download a file from Firebase Storage to a local temp file.

        Returns the local file path.
        Raises FileDownloadError on failure.
        """
        if not suffix:
            suffix = Path(storage_path).suffix or ".tmp"

        fd, local_path = tempfile.mkstemp(suffix=suffix)
        os.close(fd)
        try:
            blob = self.get_bucket().blob(storage_path)
            blob.download_to_filename(local_path)
            logger.info("Downloaded %s -> %s", storage_path, local_path)
            return local_path
        except Exception as exc:
            os.unlink(local_path)
            logger.error("Failed to download %s: %s", storage_path, exc)
            raise FileDownloadError(detail=f"Failed to download {storage_path}: {exc}") from exc

    async def upload_from_file(self, local_path: str, storage_path: str, content_type: Optional[str] = None) -> str:
        """Upload a local file to Firebase Storage.

        Returns the storage path.
        Raises FileUploadError on failure.
        """
        try:
            blob = self.get_bucket().blob(storage_path)
            blob.upload_from_filename(local_path, content_type=content_type)
            blob.make_public()
            url = blob.public_url
            logger.info("Uploaded %s -> %s (url=%s)", local_path, storage_path, url)
            return url
        except Exception as exc:
            logger.error("Failed to upload %s to %s: %s", local_path, storage_path, exc)
            raise FileUploadError(detail=f"Failed to upload to {storage_path}: {exc}") from exc

    async def upload_bytes(self, data: bytes, storage_path: str, content_type: str = "application/octet-stream") -> str:
        """Upload raw bytes to Firebase Storage. Returns public URL."""
        try:
            blob = self.get_bucket().blob(storage_path)
            blob.upload_from_string(data, content_type=content_type)
            blob.make_public()
            return blob.public_url
        except Exception as exc:
            raise FileUploadError(detail=f"Failed to upload bytes to {storage_path}: {exc}") from exc

    async def delete(self, storage_path: str) -> None:
        """Delete a file from Firebase Storage."""
        try:
            blob = self.get_bucket().blob(storage_path)
            blob.delete()
            logger.info("Deleted storage path %s", storage_path)
        except Exception as exc:
            logger.warning("Could not delete %s: %s", storage_path, exc)

    async def get_download_url(self, storage_path: str) -> str:
        """Get a signed / public download URL for a storage path."""
        try:
            blob = self.get_bucket().blob(storage_path)
            blob.make_public()
            return blob.public_url
        except Exception as exc:
            raise FileDownloadError(detail=f"Failed to get download URL for {storage_path}: {exc}") from exc
