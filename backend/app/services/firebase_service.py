"""Firebase Firestore service: high-level wrappers."""

from __future__ import annotations

import logging
from typing import Any, Dict, Optional

from app.core.constants import JobStatus
from app.repositories.job_repository import JobRepository
from app.repositories.user_repository import UserRepository

logger = logging.getLogger(__name__)


class FirebaseService:
    """High-level service for Firebase operations used by API handlers."""

    def __init__(self) -> None:
        self._job_repo = JobRepository()
        self._user_repo = UserRepository()

    async def get_or_create_user(
        self, uid: str, email: Optional[str] = None, display_name: Optional[str] = None
    ) -> Dict[str, Any]:
        return await self._user_repo.get_or_create_user(uid, email, display_name)

    async def create_job(self, job_dict: Dict[str, Any]) -> None:
        await self._job_repo.create_job(job_dict)

    async def get_job(self, job_id: str) -> Optional[Dict[str, Any]]:
        return await self._job_repo.get_job(job_id)

    async def update_job_status(
        self,
        job_id: str,
        status: JobStatus,
        progress: int = 0,
        error_message: Optional[str] = None,
        result_urls: Optional[Dict[str, str]] = None,
    ) -> None:
        await self._job_repo.update_job_status(job_id, status, progress, error_message, result_urls)

    async def list_user_jobs(self, user_id: str, limit: int = 50):
        return await self._job_repo.list_jobs_for_user(user_id, limit)
