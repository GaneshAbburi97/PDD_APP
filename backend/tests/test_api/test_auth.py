"""Tests for authentication API endpoints."""

from __future__ import annotations

from unittest.mock import patch


def test_health_endpoint_public(app_client):
    """Health endpoint must be accessible without authentication."""
    resp = app_client.get("/health")
    assert resp.status_code == 200
    data = resp.json()
    assert data["status"] == "ok"
    assert "version" in data


def test_auth_me_endpoint(app_client):
    """Authenticated /auth/me should return user claims."""
    resp = app_client.get(
        "/api/v1/auth/me",
        headers={"Authorization": "Bearer fake-test-token"},
    )
    assert resp.status_code == 200
    data = resp.json()
    assert data["uid"] == "test-user-123"


def test_auth_me_no_token(app_client):
    """Requests without Authorization header should be rejected by middleware."""
    # Middleware is active; without a token it should return 401
    with patch("app.core.security.verify_firebase_token", side_effect=Exception("no token")):
        resp = app_client.get("/api/v1/auth/me")
    assert resp.status_code in (401, 422)
