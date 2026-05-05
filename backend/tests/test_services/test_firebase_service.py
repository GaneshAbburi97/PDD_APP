"""Tests for Firebase and job services."""

from __future__ import annotations

import pytest
from unittest.mock import AsyncMock, MagicMock, patch


@pytest.mark.asyncio
async def test_firebase_service_get_or_create_user():
    with patch("app.services.firebase_service.UserRepository") as MockRepo:
        MockRepo.return_value.get_or_create_user = AsyncMock(return_value={"uid": "u1"})
        from app.services.firebase_service import FirebaseService
        svc = FirebaseService()
        result = await svc.get_or_create_user("u1", "u1@test.com")
    assert result["uid"] == "u1"


@pytest.mark.asyncio
async def test_job_service_create_job():
    with patch("app.services.job_service.FirebaseService") as MockFB, \
         patch("app.services.job_service.JobRepository"), \
         patch("app.services.job_service.StorageService"), \
         patch("app.services.job_service.ResultGeneratorService"):
        MockFB.return_value.create_job = AsyncMock()
        from app.services.job_service import JobService
        svc = JobService()
        svc._firebase.create_job = AsyncMock()
        job = await svc.create_job(
            user_id="u1",
            storage_path="uploads/u1/scan.dcm",
        )
    assert job.user_id == "u1"
    assert job.storage_path == "uploads/u1/scan.dcm"
