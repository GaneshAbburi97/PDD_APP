"""backend/app/api/v1/health.py — Health check endpoint."""

from __future__ import annotations

from fastapi import APIRouter

from app.core.config import get_settings

router = APIRouter(tags=["health"])


@router.get("/health")
async def health_check():
    """
    Returns HTTP 200 when the server is up.
    The Android app calls this to verify backend reachability before
    submitting a processing job.
    """
    settings = get_settings()
    return {
        "status": "ok",
        "version": settings.app_version,
        "model_loaded": _is_model_loaded(),
    }


def _is_model_loaded() -> bool:
    """Lazily check whether the AI model singleton is already cached."""
    try:
        from app.ai.inference import get_inference_engine
        engine = get_inference_engine()
        return engine.is_loaded
    except Exception:
        return False
