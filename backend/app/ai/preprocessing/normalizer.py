"""Intensity normalisation strategies."""

from __future__ import annotations

import numpy as np


def min_max_normalise(image: np.ndarray, eps: float = 1e-8) -> np.ndarray:
    """Normalise to [0, 1] range."""
    pmin = image.min()
    pmax = image.max()
    return (image - pmin) / (pmax - pmin + eps)


def z_score_normalise(image: np.ndarray, eps: float = 1e-8) -> np.ndarray:
    """Standardise to zero mean, unit variance."""
    mean = image.mean()
    std = image.std()
    return (image - mean) / (std + eps)


def percentile_clip(image: np.ndarray, lower: float = 1.0, upper: float = 99.0) -> np.ndarray:
    """Clip at percentile values and normalise to [0,1]."""
    low_val = np.percentile(image, lower)
    high_val = np.percentile(image, upper)
    clipped = np.clip(image, low_val, high_val)
    return min_max_normalise(clipped)
