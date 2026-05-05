"""Pydantic schemas for job operations."""

from __future__ import annotations

from datetime import datetime
from typing import Any, Dict, List, Optional

from pydantic import BaseModel, Field

from app.core.constants import JobStatus, ModelArchitecture


class CreateJobRequest(BaseModel):
    storage_path: str = Field(..., description="Firebase Storage path of the uploaded file")
    modality: str = Field(default="panoramic", description="Image modality: panoramic, cbct, dicom")
    model_architecture: ModelArchitecture = Field(
        default=ModelArchitecture.UNET, description="AI model architecture to use"
    )
    metadata: Optional[Dict[str, Any]] = Field(default=None, description="Optional device/session metadata")


class JobStatusResponse(BaseModel):
    job_id: str
    status: JobStatus
    progress: int = Field(default=0, ge=0, le=100, description="Processing progress percentage")
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
    error_message: Optional[str] = None
    result_urls: Optional[Dict[str, str]] = None
    retry_count: int = 0


class JobListResponse(BaseModel):
    jobs: List[JobStatusResponse]
    total: int


class CancelJobResponse(BaseModel):
    job_id: str
    message: str


class RetryJobResponse(BaseModel):
    job_id: str
    message: str
    new_status: JobStatus
