"""Cleanup worker stub (delegates to background_tasks)."""

from app.workers.background_tasks import periodic_cleanup

__all__ = ["periodic_cleanup"]
