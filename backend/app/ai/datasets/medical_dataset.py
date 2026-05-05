"""Medical image dataset for PyTorch DataLoader."""

from __future__ import annotations

import os
from typing import Callable, List, Optional, Tuple

import numpy as np
from torch.utils.data import Dataset

from app.ai.preprocessing.dicom_loader import load_image
from app.ai.preprocessing.image_processor import preprocess_for_inference
from app.core.constants import INFERENCE_IMAGE_SIZE


class MedicalSegmentationDataset(Dataset):
    """Dataset of (image, mask) pairs for segmentation training."""

    def __init__(
        self,
        image_paths: List[str],
        mask_paths: List[str],
        image_size: Tuple[int, int] = INFERENCE_IMAGE_SIZE,
        augmentation: Optional[Callable] = None,
    ) -> None:
        assert len(image_paths) == len(mask_paths), "Image and mask lists must have the same length"
        self.image_paths = image_paths
        self.mask_paths = mask_paths
        self.image_size = image_size
        self.augmentation = augmentation

    def __len__(self) -> int:
        return len(self.image_paths)

    def __getitem__(self, idx: int) -> dict:
        image, _ = load_image(self.image_paths[idx])
        mask, _ = load_image(self.mask_paths[idx])

        # Ensure single-channel mask in [0, 1]
        if mask.ndim == 3:
            mask = mask[..., 0]
        mask = (mask > 0.5).astype(np.float32)

        # Convert image to (H, W) float32
        if image.ndim == 3 and image.shape[2] in (3, 4):
            import cv2
            image = cv2.cvtColor((image * 255).astype(np.uint8), cv2.COLOR_RGB2GRAY).astype(np.float32) / 255.0
        elif image.ndim == 2:
            pass  # already grayscale

        if self.augmentation:
            augmented = self.augmentation(image=image, mask=mask)
            image = augmented["image"]
            mask = augmented["mask"]

        # Preprocess image → (1, H, W)
        processed = preprocess_for_inference(image, self.image_size)

        import torch
        return {
            "image": torch.from_numpy(processed),
            "mask": torch.from_numpy(mask[np.newaxis]).float(),
            "path": self.image_paths[idx],
        }
