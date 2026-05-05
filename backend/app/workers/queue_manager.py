"""Queue manager: manages multiple JobWorker instances."""

from __future__ import annotations

import asyncio
import logging
from typing import List, Optional

from app.core.config import settings
from app.workers.job_worker import JobWorker

logger = logging.getLogger(__name__)


class QueueManager:
    _instance: Optional["QueueManager"] = None

    def __new__(cls) -> "QueueManager":
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._workers: List[JobWorker] = []
            cls._instance._tasks: List[asyncio.Task] = []
        return cls._instance

    async def start(self, num_workers: Optional[int] = None) -> None:
        n = num_workers or settings.WORKER_CONCURRENCY
        logger.info("Starting %d workers", n)
        for i in range(n):
            worker = JobWorker(worker_id=f"worker-{i + 1}")
            self._workers.append(worker)
            task = asyncio.create_task(worker.start(), name=f"worker-{i + 1}")
            self._tasks.append(task)

    async def stop(self) -> None:
        logger.info("Stopping all workers")
        for worker in self._workers:
            await worker.stop()
        for task in self._tasks:
            task.cancel()
        await asyncio.gather(*self._tasks, return_exceptions=True)
        self._workers.clear()
        self._tasks.clear()

    @property
    def workers(self) -> List[JobWorker]:
        return list(self._workers)

    @property
    def active_worker_count(self) -> int:
        return sum(1 for w in self._workers if w.is_running)

    @property
    def total_worker_count(self) -> int:
        return len(self._workers)
