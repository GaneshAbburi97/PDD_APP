"""Pydantic schemas for admin endpoints."""

from __future__ import annotations

from typing import Any, Dict, List, Optional

from pydantic import BaseModel


class WorkerStatus(BaseModel):
    worker_id: str
    is_running: bool
    current_job_id: Optional[str] = None
    jobs_processed: int = 0
    jobs_failed: int = 0


class QueueStatus(BaseModel):
    pending_jobs: int
    processing_jobs: int
    workers_active: int
    workers_total: int


class MetricsResponse(BaseModel):
    queue: QueueStatus
    workers: List[WorkerStatus]
    total_jobs_today: int
    avg_processing_time_seconds: Optional[float] = None
    gpu_available: bool = False
    gpu_memory_used_mb: Optional[float] = None
    extra: Optional[Dict[str, Any]] = None


class HealthResponse(BaseModel):
    status: str = "ok"
    version: str
    firebase_connected: bool = False
    model_loaded: bool = False
    gpu_available: bool = False
    uptime_seconds: Optional[float] = None
