"""backend/app/main.py — FastAPI application entry point."""

from __future__ import annotations

import logging
import os

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.v1 import health, auth, processing
from app.core.config import get_settings
from app.core.firebase_admin import init_firebase

# ── Logging ──────────────────────────────────────────────────────────────────
logging.basicConfig(
    level=logging.DEBUG if os.getenv("DEBUG", "false").lower() == "true" else logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)
logger = logging.getLogger(__name__)

# ── App factory ──────────────────────────────────────────────────────────────
settings = get_settings()

app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    docs_url="/docs" if settings.debug else None,
    redoc_url="/redoc" if settings.debug else None,
)

# ── CORS ─────────────────────────────────────────────────────────────────────
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ── Routers ───────────────────────────────────────────────────────────────────
app.include_router(health.router)
app.include_router(auth.router, prefix="/api/v1")
app.include_router(processing.router, prefix="/api/v1")


# ── Startup ───────────────────────────────────────────────────────────────────
@app.on_event("startup")
async def startup_event() -> None:
    logger.info("🚀 Starting %s v%s", settings.app_name, settings.app_version)
    try:
        init_firebase()
    except Exception as exc:
        logger.warning("Firebase init failed (offline mode?): %s", exc)
    logger.info("✅ Server ready — listening on %s:%d", settings.host, settings.port)


# ── Entry point for direct execution ─────────────────────────────────────────
if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        "app.main:app",
        host=settings.host,
        port=settings.port,
        reload=settings.debug,
    )
