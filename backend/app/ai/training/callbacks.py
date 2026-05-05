"""Training callbacks."""

from __future__ import annotations

import logging
from typing import Any, Dict

logger = logging.getLogger(__name__)


class EarlyStopping:
    def __init__(self, patience: int = 10, min_delta: float = 1e-4) -> None:
        self.patience = patience
        self.min_delta = min_delta
        self._best = float("-inf")
        self._counter = 0

    def __call__(self, metric: float) -> bool:
        """Return True if training should stop."""
        if metric > self._best + self.min_delta:
            self._best = metric
            self._counter = 0
            return False
        self._counter += 1
        logger.debug("EarlyStopping counter %d/%d", self._counter, self.patience)
        return self._counter >= self.patience
