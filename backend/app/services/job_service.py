"""backend/app/services/job_service.py — Job lifecycle management."""

from __future__ import annotations

import asyncio
import logging
import tempfile
from pathlib import Path
from typing import Any, Dict, Optional

import httpx

from app.ai.inference import get_inference_engine
from app.core.config import get_settings
from app.core.firebase_admin import get_firestore_client

logger = logging.getLogger(__name__)


class JobService:
    """
    Manages the lifecycle of a processing job:
      1. Create job record in Firestore
      2. Download the input file from Firebase Storage
      3. Run AI inference (with timeout protection)
      4. Update job record with results
    """

    def __init__(self) -> None:
        self.settings = get_settings()
        self._cancelled: set[str] = set()

    # ── Firestore helpers ────────────────────────────────────────────────────

    def _jobs_ref(self):
        return get_firestore_client().collection("jobs")

    async def create_job(
        self,
        job_id: str,
        user_id: str,
        file_url: str,
        file_name: str,
    ) -> None:
        doc = {
            "jobId": job_id,
            "userId": user_id,
            "fileUrl": file_url,
            "fileName": file_name,
            "status": "QUEUED",
            "progress": 0,
            "errorMessage": None,
            "outputFileUrl": None,
            "createdAt": _server_timestamp(),
            "updatedAt": _server_timestamp(),
            "retryCount": 0,
        }
        loop = asyncio.get_event_loop()
        await loop.run_in_executor(None, lambda: self._jobs_ref().document(job_id).set(doc))
        logger.info("Job created: %s", job_id)

    async def get_job(self, job_id: str) -> Optional[Dict[str, Any]]:
        loop = asyncio.get_event_loop()
        snap = await loop.run_in_executor(None, lambda: self._jobs_ref().document(job_id).get())
        if not snap.exists:
            return None
        data = snap.to_dict()
        # Normalise keys for the API response
        return {
            "job_id": data.get("jobId", job_id),
            "status": data.get("status", "UNKNOWN"),
            "progress": data.get("progress", 0),
            "error": data.get("errorMessage"),
            "output_url": data.get("outputFileUrl"),
            "file_url": data.get("fileUrl", ""),
            "file_name": data.get("fileName", ""),
        }

    async def cancel_job(self, job_id: str) -> bool:
        job = await self.get_job(job_id)
        if not job or job["status"] in ("COMPLETED", "FAILED"):
            return False
        self._cancelled.add(job_id)
        await self._update_job(job_id, {"status": "CANCELLED"})
        return True

    async def reset_job(self, job_id: str) -> None:
        self._cancelled.discard(job_id)
        await self._update_job(job_id, {"status": "QUEUED", "progress": 0, "errorMessage": None})

    async def _update_job(self, job_id: str, data: Dict[str, Any]) -> None:
        data["updatedAt"] = _server_timestamp()
        loop = asyncio.get_event_loop()
        await loop.run_in_executor(None, lambda: self._jobs_ref().document(job_id).update(data))

    # ── Inference pipeline ───────────────────────────────────────────────────

    async def run_inference_job(self, job_id: str, file_url: str, file_name: str) -> None:
        """
        Full inference pipeline executed as a FastAPI background task.

        Steps:
          1. Mark job as PROCESSING
          2. Download input file
          3. Run AI inference (with timeout)
          4. Update job with results or error
        """
        if job_id in self._cancelled:
            logger.info("Job %s was cancelled before inference started", job_id)
            return

        try:
            await self._update_job(job_id, {"status": "PROCESSING", "progress": 5})

            # ── Download file ────────────────────────────────────────────────
            tmp_path = await self._download_file(file_url, file_name)
            await self._update_job(job_id, {"progress": 20})

            if job_id in self._cancelled:
                return

            # ── Run inference ────────────────────────────────────────────────
            engine = get_inference_engine()
            try:
                result = await asyncio.wait_for(
                    asyncio.get_event_loop().run_in_executor(
                        None, lambda: engine.infer(str(tmp_path))
                    ),
                    timeout=self.settings.inference_timeout_seconds,
                )
            except asyncio.TimeoutError:
                raise RuntimeError(
                    f"Inference timed out after {self.settings.inference_timeout_seconds}s"
                )

            await self._update_job(job_id, {"progress": 90})

            # ── Persist results ──────────────────────────────────────────────
            await self._update_job(
                job_id,
                {
                    "status": "COMPLETED",
                    "progress": 100,
                    "outputFileUrl": result.get("output_url", ""),
                    "inferenceResult": result,
                },
            )
            logger.info("Job %s completed successfully", job_id)

        except Exception as exc:
            logger.exception("Job %s failed: %s", job_id, exc)
            await self._update_job(
                job_id,
                {"status": "FAILED", "errorMessage": str(exc)},
            )
        finally:
            # Clean up temp files
            _cleanup_temp(Path(self.settings.temp_dir) / file_name)

    # ── File download ────────────────────────────────────────────────────────

    async def _download_file(self, url: str, file_name: str) -> Path:
        dest = Path(self.settings.temp_dir) / file_name
        async with httpx.AsyncClient(timeout=120) as client:
            async with client.stream("GET", url) as response:
                response.raise_for_status()
                with open(dest, "wb") as f:
                    async for chunk in response.aiter_bytes(chunk_size=1024 * 1024):
                        f.write(chunk)
        logger.debug("Downloaded %s to %s", url, dest)
        return dest


# ── Module-level singleton ───────────────────────────────────────────────────

_job_service: Optional[JobService] = None


def get_job_service() -> JobService:
    global _job_service
    if _job_service is None:
        _job_service = JobService()
    return _job_service


# ── Utilities ────────────────────────────────────────────────────────────────

def _server_timestamp():
    from google.cloud.firestore import SERVER_TIMESTAMP
    return SERVER_TIMESTAMP


def _cleanup_temp(path: Path) -> None:
    try:
        if path.exists():
            path.unlink()
    except Exception as exc:
        logger.warning("Could not remove temp file %s: %s", path, exc)
