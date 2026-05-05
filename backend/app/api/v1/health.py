"""Health check endpoint."""

from __future__ import annotations

import time

from fastapi import APIRouter

from app.ai.inference.gpu_utils import is_gpu_available
from app.core.config import settings
from app.schemas.admin_schemas import HealthResponse

router = APIRouter(tags=["health"])

_start_time = time.monotonic()


@router.get("/health", response_model=HealthResponse, include_in_schema=True)
async def health_check() -> HealthResponse:
    """Public health-check endpoint."""
    firebase_ok = False
    try:
        from app.core.firebase_admin import initialize_firebase
        initialize_firebase()
        firebase_ok = True
    except Exception:
        pass

    return HealthResponse(
        status="ok",
        version=settings.APP_VERSION,
        firebase_connected=firebase_ok,
        gpu_available=is_gpu_available(),
        uptime_seconds=round(time.monotonic() - _start_time, 1),
    )
