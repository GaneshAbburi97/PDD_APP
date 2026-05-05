"""Background tasks: periodic cleanup etc."""

from __future__ import annotations

import asyncio
import logging

from app.utils.temp_file_manager import cleanup_old_temp_files

logger = logging.getLogger(__name__)


async def periodic_cleanup(interval_seconds: float = 3600.0) -> None:
    """Periodically remove old temporary files."""
    while True:
        try:
            removed = await cleanup_old_temp_files(max_age_seconds=interval_seconds)
            if removed:
                logger.info("Cleaned up %d old temp directories", removed)
        except Exception as exc:
            logger.warning("Cleanup task error: %s", exc)
        await asyncio.sleep(interval_seconds)
