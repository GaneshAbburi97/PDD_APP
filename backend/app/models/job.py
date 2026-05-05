"""Domain model: ProcessingJob."""

from __future__ import annotations

from datetime import datetime
from typing import Any, Dict, Optional

from app.core.constants import JobStatus, ModelArchitecture


class ProcessingJob:
    """In-memory representation of a processing job."""

    def __init__(
        self,
        job_id: str,
        user_id: str,
        storage_path: str,
        modality: str = "panoramic",
        model_architecture: ModelArchitecture = ModelArchitecture.UNET,
        metadata: Optional[Dict[str, Any]] = None,
    ) -> None:
        self.job_id = job_id
        self.user_id = user_id
        self.storage_path = storage_path
        self.modality = modality
        self.model_architecture = model_architecture
        self.metadata = metadata or {}
        self.status: JobStatus = JobStatus.PENDING
        self.progress: int = 0
        self.retry_count: int = 0
        self.error_message: Optional[str] = None
        self.result_urls: Optional[Dict[str, str]] = None
        self.created_at: datetime = datetime.utcnow()
        self.updated_at: datetime = datetime.utcnow()

    def to_dict(self) -> Dict[str, Any]:
        return {
            "job_id": self.job_id,
            "user_id": self.user_id,
            "storage_path": self.storage_path,
            "modality": self.modality,
            "model_architecture": self.model_architecture.value,
            "metadata": self.metadata,
            "status": self.status.value,
            "progress": self.progress,
            "retry_count": self.retry_count,
            "error_message": self.error_message,
            "result_urls": self.result_urls,
            "created_at": self.created_at.isoformat(),
            "updated_at": self.updated_at.isoformat(),
        }

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> "ProcessingJob":
        job = cls(
            job_id=data["job_id"],
            user_id=data["user_id"],
            storage_path=data["storage_path"],
            modality=data.get("modality", "panoramic"),
            model_architecture=ModelArchitecture(data.get("model_architecture", "unet")),
            metadata=data.get("metadata"),
        )
        job.status = JobStatus(data.get("status", "pending"))
        job.progress = data.get("progress", 0)
        job.retry_count = data.get("retry_count", 0)
        job.error_message = data.get("error_message")
        job.result_urls = data.get("result_urls")
        return job
