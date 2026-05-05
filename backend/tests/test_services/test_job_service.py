"""Tests for JobService."""

from __future__ import annotations

import pytest
from unittest.mock import AsyncMock, MagicMock, patch


@pytest.mark.asyncio
async def test_cancel_job_not_found():
    from app.core.exceptions import JobNotFoundError
    from app.services.job_service import JobService

    with patch("app.services.job_service.FirebaseService") as MockFB, \
         patch("app.services.job_service.JobRepository"), \
         patch("app.services.job_service.StorageService"), \
         patch("app.services.job_service.ResultGeneratorService"):
        MockFB.return_value.get_job = AsyncMock(return_value=None)
        svc = JobService()
        svc._firebase.get_job = AsyncMock(return_value=None)

        with pytest.raises(JobNotFoundError):
            await svc.cancel_job("no-such-job", "user-1")
