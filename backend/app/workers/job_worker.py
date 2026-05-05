"""Async background job worker."""

from __future__ import annotations

import asyncio
import logging
import time
from typing import Optional

from app.core.config import settings
from app.core.constants import JOB_MAX_RETRIES, JOB_PROCESSING_TIMEOUT_SECONDS, JOB_RETRY_BACKOFF_BASE, JobStatus
from app.repositories.job_repository import JobRepository
from app.services.job_service import JobService
from app.utils.metrics_collector import metrics

logger = logging.getLogger(__name__)


class JobWorker:
    """Polls Firestore for pending jobs and processes them."""

    def __init__(self, worker_id: str = "worker-1") -> None:
        self.worker_id = worker_id
        self._running = False
        self._current_job_id: Optional[str] = None
        self._jobs_processed = 0
        self._jobs_failed = 0
        self._job_repo = JobRepository()
        self._job_service = JobService()

    @property
    def is_running(self) -> bool:
        return self._running

    @property
    def current_job_id(self) -> Optional[str]:
        return self._current_job_id

    @property
    def jobs_processed(self) -> int:
        return self._jobs_processed

    @property
    def jobs_failed(self) -> int:
        return self._jobs_failed

    async def start(self) -> None:
        self._running = True
        logger.info("Worker %s started", self.worker_id)
        while self._running:
            try:
                await self._poll_and_process()
            except Exception as exc:
                logger.error("Worker %s unhandled error: %s", self.worker_id, exc, exc_info=True)
            await asyncio.sleep(settings.WORKER_POLL_INTERVAL_SECONDS)

    async def stop(self) -> None:
        self._running = False
        logger.info("Worker %s stopped", self.worker_id)

    async def _poll_and_process(self) -> None:
        pending = await self._job_repo.list_pending_jobs(limit=1)
        if not pending:
            return

        job_doc = pending[0]
        job_id = job_doc.get("job_id") or job_doc.get("id")
        retry_count = job_doc.get("retry_count", 0)

        if not job_id:
            return

        # Mark as processing before starting
        await self._job_repo.update_job_status(job_id, JobStatus.PROCESSING, progress=0)
        self._current_job_id = job_id

        t0 = time.monotonic()
        try:
            await asyncio.wait_for(
                self._job_service.process_job(job_id),
                timeout=JOB_PROCESSING_TIMEOUT_SECONDS,
            )
            elapsed = time.monotonic() - t0
            self._jobs_processed += 1
            metrics.record_job_completed(elapsed)
            logger.info("Worker %s finished job %s in %.2fs", self.worker_id, job_id, elapsed)

        except asyncio.TimeoutError:
            logger.error("Worker %s: job %s timed out", self.worker_id, job_id)
            await self._handle_failure(job_id, retry_count, "Job processing timed out.")
        except Exception as exc:
            logger.error("Worker %s: job %s failed: %s", self.worker_id, job_id, exc)
            await self._handle_failure(job_id, retry_count, str(exc))
        finally:
            self._current_job_id = None

    async def _handle_failure(self, job_id: str, retry_count: int, error_message: str) -> None:
        self._jobs_failed += 1
        metrics.record_job_failed()

        if retry_count < JOB_MAX_RETRIES:
            backoff = JOB_RETRY_BACKOFF_BASE ** retry_count
            logger.info("Scheduling retry %d for job %s in %ds", retry_count + 1, job_id, backoff)
            await self._job_repo.update_job_status(job_id, JobStatus.PENDING, error_message=error_message)
            await self._job_repo.increment_retry(job_id)
            await asyncio.sleep(backoff)
        else:
            await self._job_repo.update_job_status(
                job_id, JobStatus.FAILED, error_message=f"Max retries exceeded. Last error: {error_message}"
            )
