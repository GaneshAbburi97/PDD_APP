"""Tests for admin endpoints."""

from __future__ import annotations

from unittest.mock import AsyncMock, MagicMock, patch


def test_metrics_endpoint(app_client):
    """GET /api/v1/admin/metrics should return metrics structure."""
    with patch("app.api.v1.admin.QueueManager") as MockQM, \
         patch("app.api.v1.admin.JobRepository") as MockRepo:
        MockQM.return_value.workers = []
        MockQM.return_value.active_worker_count = 0
        MockQM.return_value.total_worker_count = 0
        MockRepo.return_value.list_pending_jobs = AsyncMock(return_value=[])

        resp = app_client.get(
            "/api/v1/admin/metrics",
            headers={"Authorization": "Bearer fake-token"},
        )

    assert resp.status_code == 200
    data = resp.json()
    assert "queue" in data
    assert "workers" in data
