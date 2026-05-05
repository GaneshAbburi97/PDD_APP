"""Image preprocessing utilities for IAN segmentation."""

from __future__ import annotations

import logging
from typing import Tuple

import cv2  # type: ignore
import numpy as np

from app.core.constants import INFERENCE_IMAGE_SIZE

logger = logging.getLogger(__name__)


def resize_image(image: np.ndarray, target_size: Tuple[int, int] = INFERENCE_IMAGE_SIZE) -> np.ndarray:
    """Resize image to target_size (H, W).  Handles 2D and 3D inputs."""
    h, w = target_size
    if image.ndim == 2:
        return cv2.resize(image, (w, h), interpolation=cv2.INTER_LINEAR)
    if image.ndim == 3 and image.shape[2] in (1, 3, 4):
        return cv2.resize(image, (w, h), interpolation=cv2.INTER_LINEAR)
    # Volume: resize each slice
    resized = np.stack([cv2.resize(image[i], (w, h)) for i in range(image.shape[0])])
    return resized


def to_grayscale(image: np.ndarray) -> np.ndarray:
    """Convert RGB/RGBA to single-channel grayscale."""
    if image.ndim == 2:
        return image
    if image.ndim == 3:
        if image.shape[2] == 1:
            return image[:, :, 0]
        if image.shape[2] == 4:
            image = image[:, :, :3]
        return cv2.cvtColor((image * 255).astype(np.uint8), cv2.COLOR_RGB2GRAY).astype(np.float32) / 255.0
    return image


def apply_clahe(image: np.ndarray, clip_limit: float = 2.0, tile_grid_size: Tuple[int, int] = (8, 8)) -> np.ndarray:
    """Apply CLAHE contrast enhancement to a grayscale image [0,1]."""
    img_u8 = (np.clip(image, 0, 1) * 255).astype(np.uint8)
    clahe = cv2.createCLAHE(clipLimit=clip_limit, tileGridSize=tile_grid_size)
    enhanced = clahe.apply(img_u8)
    return enhanced.astype(np.float32) / 255.0


def normalise_zero_mean(image: np.ndarray) -> np.ndarray:
    """Standardise image to zero mean, unit std."""
    mean = image.mean()
    std = image.std()
    if std < 1e-6:
        return image - mean
    return (image - mean) / std


def preprocess_for_inference(
    image: np.ndarray,
    target_size: Tuple[int, int] = INFERENCE_IMAGE_SIZE,
    apply_contrast: bool = True,
) -> np.ndarray:
    """Full preprocessing pipeline: resize → grayscale → CLAHE → normalise → CHW tensor shape.

    Returns float32 numpy array of shape (1, H, W) ready for model input.
    """
    # Ensure float32 in [0, 1]
    image = image.astype(np.float32)
    if image.max() > 1.0:
        image = image / 255.0

    # Grayscale
    image = to_grayscale(image)

    # Resize
    image = resize_image(image, target_size)

    # CLAHE contrast enhancement
    if apply_contrast:
        image = apply_clahe(image)

    # Normalise
    image = normalise_zero_mean(image)

    # Add channel dim → (1, H, W)
    image = image[np.newaxis, :, :]
    logger.debug("Preprocessed image shape: %s", image.shape)
    return image
