"""GPU utilities for inference."""

from __future__ import annotations

import logging
from typing import Optional, Tuple

import torch

logger = logging.getLogger(__name__)


def select_device(requested: str = "auto") -> torch.device:
    """Select the best available device.

    Args:
        requested: 'auto' | 'cpu' | 'cuda' | 'cuda:0' etc.

    Returns:
        torch.device
    """
    if requested == "auto":
        device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    else:
        device = torch.device(requested)

    logger.info("Inference device: %s", device)
    return device


def get_gpu_memory_info() -> Optional[Tuple[float, float]]:
    """Return (used_mb, total_mb) for the first CUDA device, or None."""
    if not torch.cuda.is_available():
        return None
    try:
        used = torch.cuda.memory_allocated(0) / 1024**2
        total = torch.cuda.get_device_properties(0).total_memory / 1024**2
        return used, total
    except Exception:
        return None


def is_gpu_available() -> bool:
    return torch.cuda.is_available()
