"""Simple metrics collector (in-memory, suitable for single-worker deployments)."""

from __future__ import annotations

import time
from collections import deque
from typing import Deque, Optional


class MetricsCollector:
    _instance: Optional["MetricsCollector"] = None

    def __new__(cls) -> "MetricsCollector":
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._init()
        return cls._instance

    def _init(self) -> None:
        self.jobs_processed: int = 0
        self.jobs_failed: int = 0
        self._processing_times: Deque[float] = deque(maxlen=1000)
        self._start_time: float = time.monotonic()

    def record_job_completed(self, processing_time_seconds: float) -> None:
        self.jobs_processed += 1
        self._processing_times.append(processing_time_seconds)

    def record_job_failed(self) -> None:
        self.jobs_failed += 1

    @property
    def avg_processing_time(self) -> Optional[float]:
        if not self._processing_times:
            return None
        return sum(self._processing_times) / len(self._processing_times)

    @property
    def uptime_seconds(self) -> float:
        return time.monotonic() - self._start_time


metrics = MetricsCollector()
