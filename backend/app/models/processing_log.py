"""Domain model: ProcessingLog."""

from __future__ import annotations

from datetime import datetime
from typing import Any, Dict, Optional


class ProcessingLog:
    def __init__(
        self,
        log_id: str,
        job_id: str,
        user_id: str,
        event: str,
        details: Optional[Dict[str, Any]] = None,
        timestamp: Optional[datetime] = None,
    ) -> None:
        self.log_id = log_id
        self.job_id = job_id
        self.user_id = user_id
        self.event = event
        self.details = details or {}
        self.timestamp = timestamp or datetime.utcnow()

    def to_dict(self) -> Dict[str, Any]:
        return {
            "log_id": self.log_id,
            "job_id": self.job_id,
            "user_id": self.user_id,
            "event": self.event,
            "details": self.details,
            "timestamp": self.timestamp.isoformat(),
        }
