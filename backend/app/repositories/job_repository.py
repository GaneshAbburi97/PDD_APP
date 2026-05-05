"""Job Firestore repository."""

from __future__ import annotations

import logging
from datetime import datetime
from typing import Any, Dict, List, Optional

from app.core.constants import COLLECTION_JOBS, JobStatus
from app.repositories.firestore_repository import FirestoreRepository

logger = logging.getLogger(__name__)


class JobRepository(FirestoreRepository):
    def __init__(self) -> None:
        super().__init__(COLLECTION_JOBS)

    async def create_job(self, job_dict: Dict[str, Any]) -> None:
        job_dict.setdefault("created_at", datetime.utcnow().isoformat())
        job_dict.setdefault("updated_at", datetime.utcnow().isoformat())
        await self.create(job_dict["job_id"], job_dict)
        logger.info("Created job %s in Firestore", job_dict["job_id"])

    async def get_job(self, job_id: str) -> Optional[Dict[str, Any]]:
        return await self.get(job_id)

    async def update_job_status(
        self,
        job_id: str,
        status: JobStatus,
        progress: int = 0,
        error_message: Optional[str] = None,
        result_urls: Optional[Dict[str, str]] = None,
    ) -> None:
        payload: Dict[str, Any] = {
            "status": status.value,
            "progress": progress,
            "updated_at": datetime.utcnow().isoformat(),
        }
        if error_message is not None:
            payload["error_message"] = error_message
        if result_urls is not None:
            payload["result_urls"] = result_urls
        await self.update(job_id, payload)
        logger.debug("Job %s -> status=%s progress=%d", job_id, status.value, progress)

    async def increment_retry(self, job_id: str) -> None:
        doc = await self.get(job_id)
        current = doc.get("retry_count", 0) if doc else 0
        await self.update(job_id, {"retry_count": current + 1, "updated_at": datetime.utcnow().isoformat()})

    async def list_jobs_for_user(
        self, user_id: str, limit: int = 50
    ) -> List[Dict[str, Any]]:
        return await self.list(
            filters=[("user_id", "==", user_id)],
            order_by="created_at",
            limit=limit,
        )

    async def list_pending_jobs(self, limit: int = 100) -> List[Dict[str, Any]]:
        return await self.list(
            filters=[("status", "==", JobStatus.PENDING.value)],
            order_by="created_at",
            limit=limit,
        )
