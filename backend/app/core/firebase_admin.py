"""backend/app/core/firebase_admin.py — Singleton Firebase Admin initialisation."""

from __future__ import annotations

import logging
from pathlib import Path
from typing import Optional

import firebase_admin
from firebase_admin import credentials, firestore, storage

from app.core.config import get_settings

logger = logging.getLogger(__name__)

_firebase_app: Optional[firebase_admin.App] = None


def init_firebase() -> firebase_admin.App:
    """Initialise Firebase Admin SDK (idempotent — safe to call multiple times)."""
    global _firebase_app

    if _firebase_app is not None:
        return _firebase_app

    settings = get_settings()
    creds_path = Path(settings.firebase_credentials_path)

    if creds_path.exists():
        cred = credentials.Certificate(str(creds_path))
        logger.info("Firebase: using service account from %s", creds_path)
    else:
        # Fall back to Application Default Credentials (useful in CI / Cloud Run)
        cred = credentials.ApplicationDefault()
        logger.info("Firebase: using Application Default Credentials")

    _firebase_app = firebase_admin.initialize_app(
        cred,
        {"storageBucket": settings.firebase_storage_bucket} if settings.firebase_storage_bucket else {},
    )
    logger.info("Firebase Admin SDK initialised ✓")
    return _firebase_app


def get_firestore_client():
    """Return a Firestore client (initialises Firebase if necessary)."""
    init_firebase()
    return firestore.client()


def get_storage_bucket():
    """Return the default Firebase Storage bucket (initialises Firebase if necessary)."""
    init_firebase()
    return storage.bucket()
