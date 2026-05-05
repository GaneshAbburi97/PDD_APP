"""Confidence heatmap generator."""

from __future__ import annotations

import io

import cv2  # type: ignore
import numpy as np
from PIL import Image


def generate_heatmap(prob_map: np.ndarray, colormap: int = cv2.COLORMAP_JET) -> bytes:
    """Convert a probability map to a coloured heatmap PNG.

    Args:
        prob_map: float32 array in [0, 1], shape (H, W).
        colormap: OpenCV colourmap constant.

    Returns:
        PNG bytes.
    """
    # Normalise to [0, 255]
    norm = (np.clip(prob_map, 0, 1) * 255).astype(np.uint8)
    heatmap = cv2.applyColorMap(norm, colormap)  # BGR
    heatmap_rgb = cv2.cvtColor(heatmap, cv2.COLOR_BGR2RGB)
    img = Image.fromarray(heatmap_rgb, mode="RGB")
    buf = io.BytesIO()
    img.save(buf, format="PNG")
    return buf.getvalue()
