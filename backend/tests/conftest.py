"""Test fixtures and shared configuration."""

from __future__ import annotations

import pytest
from fastapi.testclient import TestClient
from unittest.mock import MagicMock, patch

# Pre-import modules so patch can resolve them correctly
import app.core.firebase_admin as _firebase_admin_mod  # noqa: F401
import app.core.security as _security_mod  # noqa: F401


@pytest.fixture(scope="session")
def mock_firebase_claims():
    return {
        "uid": "test-user-123",
        "email": "test@example.com",
        "name": "Test User",
        "email_verified": True,
    }


@pytest.fixture()
def app_client(mock_firebase_claims):
    """FastAPI test client with Firebase auth mocked."""
    with patch("app.core.firebase_admin.initialize_firebase", return_value=MagicMock()):
        with patch("app.core.security.verify_firebase_token", return_value=mock_firebase_claims):
            from app.main import create_app
            test_app = create_app()
            with TestClient(test_app, raise_server_exceptions=False) as client:
                yield client
