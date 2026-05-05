"""Data augmentation helpers using Albumentations."""

from __future__ import annotations

from typing import Optional

import numpy as np


def get_train_augmentation(image_size: int = 512):
    """Return an Albumentations Compose pipeline for training augmentation."""
    try:
        import albumentations as A
        from albumentations.pytorch import ToTensorV2
    except ImportError:
        return None

    return A.Compose(
        [
            A.RandomRotate90(p=0.5),
            A.Flip(p=0.5),
            A.ShiftScaleRotate(shift_limit=0.05, scale_limit=0.1, rotate_limit=15, p=0.5),
            A.RandomBrightnessContrast(brightness_limit=0.2, contrast_limit=0.2, p=0.5),
            A.GaussNoise(var_limit=(5.0, 50.0), p=0.3),
            A.Resize(image_size, image_size),
        ],
        additional_targets={"mask": "mask"},
    )


def get_val_augmentation(image_size: int = 512):
    """Minimal (resize-only) pipeline for validation."""
    try:
        import albumentations as A
    except ImportError:
        return None

    return A.Compose(
        [A.Resize(image_size, image_size)],
        additional_targets={"mask": "mask"},
    )
