"""FastAPI application factory."""

from __future__ import annotations

import asyncio
import logging
from contextlib import asynccontextmanager
from typing import AsyncGenerator

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.api.v1 import admin, auth, health, processing
from app.core.config import settings
from app.core.exceptions import AppBaseException
from app.core.firebase_admin import initialize_firebase
from app.middleware.auth_middleware import AuthMiddleware
from app.middleware.logging_middleware import LoggingMiddleware
from app.middleware.rate_limit_middleware import RateLimitMiddleware
from app.utils.logger import configure_logging
from app.workers.background_tasks import periodic_cleanup
from app.workers.queue_manager import QueueManager

logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncGenerator[None, None]:
    """Startup and shutdown lifecycle."""
    configure_logging()
    logger.info("Starting %s v%s", settings.APP_NAME, settings.APP_VERSION)

    # Initialise Firebase
    try:
        initialize_firebase()
        logger.info("Firebase Admin SDK initialised")
    except Exception as exc:
        logger.warning("Firebase initialisation failed (non-fatal in dev mode): %s", exc)

    # Start background workers
    manager = QueueManager()
    await manager.start()

    # Start cleanup task
    cleanup_task = asyncio.create_task(periodic_cleanup())

    yield

    # Shutdown
    logger.info("Shutting down workers...")
    await manager.stop()
    cleanup_task.cancel()
    logger.info("Shutdown complete")


def create_app() -> FastAPI:
    app = FastAPI(
        title=settings.APP_NAME,
        version=settings.APP_VERSION,
        description="Medical AI Platform – IAN segmentation backend",
        docs_url="/docs",
        redoc_url="/redoc",
        openapi_url="/openapi.json",
        lifespan=lifespan,
    )

    # ── CORS ──────────────────────────────────────────────────────────────────
    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.CORS_ORIGINS,
        allow_credentials=settings.CORS_ALLOW_CREDENTIALS,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # ── Custom middleware (applied in reverse order) ──────────────────────────
    app.add_middleware(LoggingMiddleware)
    app.add_middleware(RateLimitMiddleware)
    app.add_middleware(AuthMiddleware)

    # ── Exception handlers ────────────────────────────────────────────────────
    @app.exception_handler(AppBaseException)
    async def app_exception_handler(request: Request, exc: AppBaseException) -> JSONResponse:
        return JSONResponse(
            status_code=exc.status_code,
            content={"detail": exc.detail},
        )

    @app.exception_handler(Exception)
    async def generic_exception_handler(request: Request, exc: Exception) -> JSONResponse:
        logger.error("Unhandled exception: %s", exc, exc_info=True)
        return JSONResponse(
            status_code=500,
            content={"detail": "An internal server error occurred."},
        )

    # ── Routers ───────────────────────────────────────────────────────────────
    app.include_router(health.router)
    app.include_router(auth.router, prefix="/api/v1")
    app.include_router(processing.router, prefix="/api/v1")
    app.include_router(admin.router, prefix="/api/v1")

    return app


app = create_app()
