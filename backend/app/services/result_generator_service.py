"""Result generator service: uploads inference artefacts to Firebase Storage."""

from __future__ import annotations

import logging
from typing import Any, Dict, Optional

from app.core.constants import (
    RESULT_HEATMAP_FILENAME,
    RESULT_MASK_FILENAME,
    RESULT_OVERLAY_FILENAME,
    RESULT_REPORT_FILENAME,
    STORAGE_RESULTS_PREFIX,
)
from app.services.storage_service import StorageService

logger = logging.getLogger(__name__)


class ResultGeneratorService:
    """Uploads inference artefacts and returns download URLs."""

    def __init__(self) -> None:
        self._storage = StorageService()

    async def upload_results(
        self,
        job_id: str,
        user_id: str,
        mask_bytes: bytes,
        overlay_bytes: bytes,
        heatmap_bytes: bytes,
        report_bytes: bytes,
    ) -> Dict[str, str]:
        """Upload all result artefacts and return a dict of download URLs."""
        base_path = f"{STORAGE_RESULTS_PREFIX}/{user_id}/{job_id}"

        urls: Dict[str, str] = {}

        upload_tasks = [
            (f"{base_path}/{RESULT_MASK_FILENAME}", mask_bytes, "image/png", "mask_url"),
            (f"{base_path}/{RESULT_OVERLAY_FILENAME}", overlay_bytes, "image/png", "overlay_url"),
            (f"{base_path}/{RESULT_HEATMAP_FILENAME}", heatmap_bytes, "image/png", "heatmap_url"),
            (f"{base_path}/{RESULT_REPORT_FILENAME}", report_bytes, "application/json", "report_url"),
        ]

        for storage_path, data, content_type, url_key in upload_tasks:
            try:
                url = await self._storage.upload_bytes(data, storage_path, content_type)
                urls[url_key] = url
                logger.debug("Uploaded %s -> %s", url_key, url)
            except Exception as exc:
                logger.error("Failed to upload %s for job %s: %s", url_key, job_id, exc)
                urls[url_key] = ""

        return urls
