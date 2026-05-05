"""Firebase Admin SDK singleton initialisation."""

from __future__ import annotations

import base64
import json
import logging
import os
from typing import Optional

import firebase_admin
from firebase_admin import credentials, firestore, storage
from firebase_admin.app import App

from app.core.config import settings

logger = logging.getLogger(__name__)

_firebase_app: Optional[App] = None


def _build_credential() -> credentials.Base:
    """Build Firebase credentials from environment configuration."""

    # 1. Path to a service-account JSON file
    if settings.GOOGLE_APPLICATION_CREDENTIALS and os.path.isfile(
        settings.GOOGLE_APPLICATION_CREDENTIALS
    ):
        logger.info("Firebase: loading credentials from file: %s", settings.GOOGLE_APPLICATION_CREDENTIALS)
        return credentials.Certificate(settings.GOOGLE_APPLICATION_CREDENTIALS)

    # 2. Inline JSON string (raw or base64-encoded)
    if settings.FIREBASE_SERVICE_ACCOUNT_JSON:
        raw = settings.FIREBASE_SERVICE_ACCOUNT_JSON.strip()
        try:
            # Try raw JSON first
            service_account_info = json.loads(raw)
        except json.JSONDecodeError:
            # Fall back to base64-encoded JSON
            service_account_info = json.loads(base64.b64decode(raw).decode())
        logger.info("Firebase: loading credentials from environment JSON")
        return credentials.Certificate(service_account_info)

    # 3. Application Default Credentials (works on GCP/Cloud Run automatically)
    logger.info("Firebase: using Application Default Credentials")
    return credentials.ApplicationDefault()


def initialize_firebase() -> App:
    """Initialise the Firebase Admin SDK (singleton).

    Safe to call multiple times – returns the existing app if already initialised.
    """
    global _firebase_app

    if _firebase_app is not None:
        return _firebase_app

    # Prevent double-init in the same process (e.g. when using hot-reload)
    try:
        _firebase_app = firebase_admin.get_app()
        logger.info("Firebase: reusing existing app instance")
        return _firebase_app
    except ValueError:
        pass

    cred = _build_credential()
    _firebase_app = firebase_admin.initialize_app(
        cred,
        {
            "storageBucket": settings.FIREBASE_STORAGE_BUCKET,
            "projectId": settings.FIREBASE_PROJECT_ID,
        },
    )
    logger.info(
        "Firebase Admin initialised. project=%s bucket=%s",
        settings.FIREBASE_PROJECT_ID,
        settings.FIREBASE_STORAGE_BUCKET,
    )
    return _firebase_app


def get_firestore_client():
    """Return a Firestore client (initialises Firebase if needed)."""
    initialize_firebase()
    return firestore.client()


def get_storage_bucket():
    """Return the default Firebase Storage bucket (initialises Firebase if needed)."""
    initialize_firebase()
    return storage.bucket()
