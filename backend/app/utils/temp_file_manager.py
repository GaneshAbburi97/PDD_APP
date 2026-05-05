"""Temporary file management utilities."""

from __future__ import annotations

import asyncio
import logging
import os
import shutil
import tempfile
from contextlib import asynccontextmanager
from pathlib import Path
from typing import AsyncGenerator, List

from app.core.config import settings

logger = logging.getLogger(__name__)


def ensure_temp_dir() -> str:
    """Create the configured temp directory if it does not exist."""
    os.makedirs(settings.TEMP_DIR, exist_ok=True)
    return settings.TEMP_DIR


def create_job_temp_dir(job_id: str) -> str:
    """Create a temporary directory scoped to a job."""
    base = ensure_temp_dir()
    job_dir = os.path.join(base, job_id)
    os.makedirs(job_dir, exist_ok=True)
    return job_dir


def cleanup_job_temp_dir(job_id: str) -> None:
    """Remove the temporary directory for a job."""
    job_dir = os.path.join(settings.TEMP_DIR, job_id)
    if os.path.isdir(job_dir):
        shutil.rmtree(job_dir, ignore_errors=True)
        logger.debug("Removed temp dir for job %s", job_id)


@asynccontextmanager
async def job_temp_context(job_id: str) -> AsyncGenerator[str, None]:
    """Async context manager that creates and cleans up a job temp dir."""
    job_dir = create_job_temp_dir(job_id)
    try:
        yield job_dir
    finally:
        loop = asyncio.get_event_loop()
        await loop.run_in_executor(None, cleanup_job_temp_dir, job_id)


async def cleanup_old_temp_files(max_age_seconds: float = 3600.0) -> int:
    """Remove job temp dirs older than max_age_seconds. Returns number removed."""
    base = ensure_temp_dir()
    removed = 0
    now = asyncio.get_event_loop().time()
    for entry in os.scandir(base):
        if entry.is_dir():
            try:
                age = now - entry.stat().st_mtime
                if age > max_age_seconds:
                    shutil.rmtree(entry.path, ignore_errors=True)
                    removed += 1
            except OSError:
                pass
    return removed
