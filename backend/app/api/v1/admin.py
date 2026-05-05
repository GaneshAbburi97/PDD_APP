"""Admin endpoints: metrics, worker status, queue status."""

from __future__ import annotations

import logging

from fastapi import APIRouter, Request

from app.ai.inference.gpu_utils import get_gpu_memory_info, is_gpu_available
from app.core.config import settings
from app.repositories.job_repository import JobRepository
from app.schemas.admin_schemas import MetricsResponse, QueueStatus, WorkerStatus
from app.utils.metrics_collector import metrics
from app.workers.queue_manager import QueueManager

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/admin", tags=["admin"])


@router.get("/metrics", response_model=MetricsResponse)
async def get_metrics(request: Request) -> MetricsResponse:
    """Return system and queue metrics (admin only)."""
    manager = QueueManager()
    job_repo = JobRepository()

    pending_docs = await job_repo.list_pending_jobs(limit=1000)
    pending_count = len(pending_docs)

    gpu_info = get_gpu_memory_info()
    gpu_used_mb = gpu_info[0] if gpu_info else None

    worker_statuses = [
        WorkerStatus(
            worker_id=w.worker_id,
            is_running=w.is_running,
            current_job_id=w.current_job_id,
            jobs_processed=w.jobs_processed,
            jobs_failed=w.jobs_failed,
        )
        for w in manager.workers
    ]

    return MetricsResponse(
        queue=QueueStatus(
            pending_jobs=pending_count,
            processing_jobs=sum(1 for w in manager.workers if w.current_job_id),
            workers_active=manager.active_worker_count,
            workers_total=manager.total_worker_count,
        ),
        workers=worker_statuses,
        total_jobs_today=metrics.jobs_processed,
        avg_processing_time_seconds=metrics.avg_processing_time,
        gpu_available=is_gpu_available(),
        gpu_memory_used_mb=gpu_used_mb,
    )
