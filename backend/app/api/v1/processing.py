"""backend/app/api/v1/processing.py — Job creation, status, and result endpoints."""

from __future__ import annotations

import logging
import uuid
from typing import Any, Dict

from fastapi import APIRouter, BackgroundTasks, Depends, HTTPException, UploadFile, status
from pydantic import BaseModel

from app.api.v1.auth import get_current_user
from app.services.job_service import JobService, get_job_service

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/process", tags=["processing"])


# ── Request / Response schemas ───────────────────────────────────────────────

class ProcessRequest(BaseModel):
    file_url: str
    file_name: str
    file_size: int
    cloud_provider: str = "FIREBASE"
    user_email: str = ""


class ProcessResponse(BaseModel):
    job_id: str
    status: str
    estimated_time_seconds: int


class JobStatusResponse(BaseModel):
    job_id: str
    status: str
    progress: int
    error: str | None = None
    output_url: str | None = None


# ── Endpoints ────────────────────────────────────────────────────────────────

@router.post("", response_model=ProcessResponse, status_code=status.HTTP_202_ACCEPTED)
async def create_job(
    body: ProcessRequest,
    background_tasks: BackgroundTasks,
    current_user: Dict[str, Any] = Depends(get_current_user),
    job_service: JobService = Depends(get_job_service),
):
    """
    Create a new processing job from a file already uploaded to Firebase Storage.
    The AI inference runs as a background task so the response is returned
    immediately.
    """
    job_id = str(uuid.uuid4())
    uid = current_user["uid"]

    await job_service.create_job(
        job_id=job_id,
        user_id=uid,
        file_url=body.file_url,
        file_name=body.file_name,
    )

    background_tasks.add_task(
        job_service.run_inference_job,
        job_id=job_id,
        file_url=body.file_url,
        file_name=body.file_name,
    )

    return ProcessResponse(
        job_id=job_id,
        status="QUEUED",
        estimated_time_seconds=30,
    )


@router.get("/status/{job_id}", response_model=JobStatusResponse)
async def get_job_status(
    job_id: str,
    current_user: Dict[str, Any] = Depends(get_current_user),
    job_service: JobService = Depends(get_job_service),
):
    """Return the current status of a processing job."""
    job = await job_service.get_job(job_id)
    if not job:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Job not found")
    return JobStatusResponse(**job)


@router.get("/result/{job_id}")
async def get_job_result(
    job_id: str,
    current_user: Dict[str, Any] = Depends(get_current_user),
    job_service: JobService = Depends(get_job_service),
):
    """Return the full result payload for a completed job."""
    job = await job_service.get_job(job_id)
    if not job:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Job not found")
    if job.get("status") != "COMPLETED":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=f"Job is not completed yet (current status: {job.get('status')})",
        )
    return job


@router.post("/cancel/{job_id}", status_code=status.HTTP_200_OK)
async def cancel_job(
    job_id: str,
    current_user: Dict[str, Any] = Depends(get_current_user),
    job_service: JobService = Depends(get_job_service),
):
    """Request cancellation of a queued or running job."""
    cancelled = await job_service.cancel_job(job_id)
    if not cancelled:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Job not found or already completed")
    return {"job_id": job_id, "status": "CANCELLED"}


@router.post("/retry/{job_id}", response_model=ProcessResponse)
async def retry_job(
    job_id: str,
    background_tasks: BackgroundTasks,
    current_user: Dict[str, Any] = Depends(get_current_user),
    job_service: JobService = Depends(get_job_service),
):
    """Re-queue a failed job for processing."""
    job = await job_service.get_job(job_id)
    if not job:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Job not found")
    if job.get("status") not in ("FAILED", "CANCELLED"):
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Only FAILED or CANCELLED jobs can be retried",
        )

    await job_service.reset_job(job_id)
    background_tasks.add_task(
        job_service.run_inference_job,
        job_id=job_id,
        file_url=job["file_url"],
        file_name=job["file_name"],
    )

    return ProcessResponse(job_id=job_id, status="QUEUED", estimated_time_seconds=30)
