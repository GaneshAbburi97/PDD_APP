"""Overlay generator: blends segmentation mask onto original image."""

from __future__ import annotations

import io

import cv2  # type: ignore
import numpy as np
from PIL import Image


def generate_overlay(
    original: np.ndarray,
    mask: np.ndarray,
    alpha: float = 0.4,
    color: tuple = (255, 0, 0),  # red overlay by default
) -> bytes:
    """Blend a binary mask onto the original image and return PNG bytes.

    Args:
        original: float32 image [0,1], shape (H, W) or (H, W, 3).
        mask: uint8 mask 0/255, shape (H, W).
        alpha: overlay transparency (0=transparent, 1=opaque).
        color: RGB colour for the overlay region.

    Returns:
        PNG bytes of the overlay image.
    """
    # Convert original to uint8 RGB
    if original.ndim == 2:
        original_rgb = cv2.cvtColor((original * 255).astype(np.uint8), cv2.COLOR_GRAY2RGB)
    else:
        original_rgb = (np.clip(original, 0, 1) * 255).astype(np.uint8)
        if original_rgb.shape[2] == 1:
            original_rgb = cv2.cvtColor(original_rgb[:, :, 0], cv2.COLOR_GRAY2RGB)

    h, w = mask.shape[:2]
    # Resize original to match mask if needed
    if original_rgb.shape[:2] != (h, w):
        original_rgb = cv2.resize(original_rgb, (w, h), interpolation=cv2.INTER_LINEAR)

    overlay = original_rgb.copy().astype(np.float32)
    mask_bool = mask > 0
    for c, ch_val in enumerate(color):
        overlay[mask_bool, c] = (1 - alpha) * overlay[mask_bool, c] + alpha * ch_val

    overlay_uint8 = np.clip(overlay, 0, 255).astype(np.uint8)
    img = Image.fromarray(overlay_uint8, mode="RGB")
    buf = io.BytesIO()
    img.save(buf, format="PNG")
    return buf.getvalue()
