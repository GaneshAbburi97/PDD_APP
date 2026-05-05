"""Mock Firebase helpers for tests."""

from __future__ import annotations

from unittest.mock import AsyncMock, MagicMock


def make_mock_firebase_service():
    svc = MagicMock()
    svc.create_job = AsyncMock()
    svc.get_job = AsyncMock(return_value={
        "job_id": "test-job-1",
        "user_id": "test-user-123",
        "status": "pending",
        "progress": 0,
        "retry_count": 0,
        "storage_path": "uploads/test-user-123/test.dcm",
    })
    svc.update_job_status = AsyncMock()
    svc.list_user_jobs = AsyncMock(return_value=[])
    return svc


def make_mock_storage_service():
    svc = MagicMock()
    svc.download_to_temp = AsyncMock(return_value="/tmp/test.dcm")
    svc.upload_bytes = AsyncMock(return_value="https://storage.googleapis.com/test/result.png")
    svc.upload_from_file = AsyncMock(return_value="https://storage.googleapis.com/test/file.dcm")
    svc.delete = AsyncMock()
    return svc
