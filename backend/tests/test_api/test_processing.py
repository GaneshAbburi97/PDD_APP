"""Tests for job processing API endpoints."""

from __future__ import annotations

from unittest.mock import AsyncMock, patch

import pytest


def test_create_job(app_client):
    """POST /api/v1/jobs should create a job and return 201."""
    with patch("app.api.v1.processing.JobService") as MockSvc:
        from app.models.job import ProcessingJob
        from app.core.constants import ModelArchitecture, JobStatus
        mock_job = ProcessingJob(
            job_id="job-abc",
            user_id="test-user-123",
            storage_path="uploads/test-user-123/scan.dcm",
        )
        MockSvc.return_value.create_job = AsyncMock(return_value=mock_job)

        resp = app_client.post(
            "/api/v1/jobs",
            json={"storage_path": "uploads/test-user-123/scan.dcm"},
            headers={"Authorization": "Bearer fake-token"},
        )
    assert resp.status_code == 201
    data = resp.json()
    assert data["job_id"] == "job-abc"
    assert data["status"] == "pending"


def test_get_job_not_found(app_client):
    """GET /api/v1/jobs/{id} for non-existent job should return 404."""
    with patch("app.api.v1.processing.JobService") as MockSvc:
        from app.core.exceptions import JobNotFoundError
        MockSvc.return_value.get_job = AsyncMock(side_effect=JobNotFoundError())

        resp = app_client.get(
            "/api/v1/jobs/nonexistent",
            headers={"Authorization": "Bearer fake-token"},
        )
    assert resp.status_code == 404


def test_list_jobs(app_client):
    """GET /api/v1/jobs should return a list of jobs."""
    with patch("app.api.v1.processing.FirebaseService") as MockSvc:
        MockSvc.return_value.list_user_jobs = AsyncMock(return_value=[])

        resp = app_client.get(
            "/api/v1/jobs",
            headers={"Authorization": "Bearer fake-token"},
        )
    assert resp.status_code == 200
    data = resp.json()
    assert "jobs" in data
    assert data["total"] == 0


def test_cancel_job(app_client):
    """POST /api/v1/jobs/{id}/cancel should cancel a job."""
    with patch("app.api.v1.processing.JobService") as MockSvc:
        MockSvc.return_value.cancel_job = AsyncMock()

        resp = app_client.post(
            "/api/v1/jobs/job-abc/cancel",
            headers={"Authorization": "Bearer fake-token"},
        )
    assert resp.status_code == 200
    data = resp.json()
    assert data["job_id"] == "job-abc"
