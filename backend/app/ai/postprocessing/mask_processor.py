"""Segmentation mask post-processing: thresholding, morphological cleanup."""

from __future__ import annotations

import logging

import cv2  # type: ignore
import numpy as np

logger = logging.getLogger(__name__)


def threshold_mask(prob_map: np.ndarray, threshold: float = 0.5) -> np.ndarray:
    """Convert a probability map to a binary mask."""
    return (prob_map >= threshold).astype(np.uint8)


def refine_mask(mask: np.ndarray, min_area: int = 100) -> np.ndarray:
    """Remove small connected components and fill holes.

    Args:
        mask: uint8 binary mask (0/1 or 0/255).
        min_area: Minimum pixel area to keep.

    Returns:
        Refined uint8 binary mask (0/255).
    """
    # Ensure 0/255
    binary = (mask > 0).astype(np.uint8) * 255

    # Remove noise with morphological opening
    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5, 5))
    opened = cv2.morphologyEx(binary, cv2.MORPH_OPEN, kernel)

    # Remove small connected components
    num_labels, labels, stats, _ = cv2.connectedComponentsWithStats(opened, connectivity=8)
    refined = np.zeros_like(opened)
    for label_idx in range(1, num_labels):  # skip background (0)
        area = stats[label_idx, cv2.CC_STAT_AREA]
        if area >= min_area:
            refined[labels == label_idx] = 255

    # Fill holes
    filled = cv2.morphologyEx(refined, cv2.MORPH_CLOSE, kernel)
    return filled


def mask_to_png_bytes(mask: np.ndarray) -> bytes:
    """Encode a binary mask as PNG bytes."""
    import io
    from PIL import Image
    # Ensure 0/255
    out = (mask > 0).astype(np.uint8) * 255
    img = Image.fromarray(out, mode="L")
    buf = io.BytesIO()
    img.save(buf, format="PNG")
    return buf.getvalue()
