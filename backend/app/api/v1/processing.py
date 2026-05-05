"""Processing / job management endpoints."""

from __future__ import annotations

import asyncio
import logging
from typing import List

from fastapi import APIRouter, BackgroundTasks, HTTPException, Request, status

from app.core.constants import JobStatus, ModelArchitecture
from app.core.exceptions import JobNotFoundError
from app.schemas.job_schemas import (
    CancelJobResponse,
    CreateJobRequest,
    JobListResponse,
    JobStatusResponse,
    RetryJobResponse,
)
from app.services.firebase_service import FirebaseService
from app.services.job_service import JobService

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/jobs", tags=["processing"])


def _job_doc_to_response(doc: dict) -> JobStatusResponse:
    return JobStatusResponse(
        job_id=doc.get("job_id", doc.get("id", "")),
        status=JobStatus(doc.get("status", "pending")),
        progress=doc.get("progress", 0),
        error_message=doc.get("error_message"),
        result_urls=doc.get("result_urls"),
        retry_count=doc.get("retry_count", 0),
    )


@router.post("", response_model=JobStatusResponse, status_code=status.HTTP_201_CREATED)
async def create_job(
    body: CreateJobRequest,
    request: Request,
    background_tasks: BackgroundTasks,
) -> JobStatusResponse:
    """Create a processing job for an already-uploaded file."""
    uid = request.state.uid
    svc = JobService()
    job = await svc.create_job(
        user_id=uid,
        storage_path=body.storage_path,
        modality=body.modality,
        model_architecture=body.model_architecture,
        metadata=body.metadata,
    )
    # Kick off processing in the background
    background_tasks.add_task(_run_job_background, job.job_id)
    return JobStatusResponse(
        job_id=job.job_id,
        status=job.status,
        progress=0,
    )


async def _run_job_background(job_id: str) -> None:
    svc = JobService()
    try:
        await svc.process_job(job_id)
    except Exception as exc:
        logger.error("Background job %s failed: %s", job_id, exc)


@router.get("", response_model=JobListResponse)
async def list_jobs(request: Request) -> JobListResponse:
    """List all jobs for the authenticated user."""
    uid = request.state.uid
    firebase = FirebaseService()
    docs = await firebase.list_user_jobs(uid)
    jobs = [_job_doc_to_response(d) for d in docs]
    return JobListResponse(jobs=jobs, total=len(jobs))


@router.get("/{job_id}", response_model=JobStatusResponse)
async def get_job_status(job_id: str, request: Request) -> JobStatusResponse:
    """Get the status of a specific job."""
    uid = request.state.uid
    svc = JobService()
    try:
        doc = await svc.get_job(job_id, uid)
    except JobNotFoundError:
        raise HTTPException(status_code=404, detail="Job not found.")
    return _job_doc_to_response(doc)


@router.post("/{job_id}/cancel", response_model=CancelJobResponse)
async def cancel_job(job_id: str, request: Request) -> CancelJobResponse:
    """Cancel a pending or processing job."""
    uid = request.state.uid
    svc = JobService()
    try:
        await svc.cancel_job(job_id, uid)
    except JobNotFoundError:
        raise HTTPException(status_code=404, detail="Job not found.")
    return CancelJobResponse(job_id=job_id, message="Job cancelled.")


@router.post("/{job_id}/retry", response_model=RetryJobResponse)
async def retry_job(job_id: str, request: Request, background_tasks: BackgroundTasks) -> RetryJobResponse:
    """Re-queue a failed job for reprocessing."""
    uid = request.state.uid
    firebase = FirebaseService()
    doc = await firebase.get_job(job_id)
    if not doc or doc.get("user_id") != uid:
        raise HTTPException(status_code=404, detail="Job not found.")

    await firebase.update_job_status(job_id, JobStatus.PENDING, progress=0)
    background_tasks.add_task(_run_job_background, job_id)
    return RetryJobResponse(job_id=job_id, message="Job re-queued.", new_status=JobStatus.PENDING)
