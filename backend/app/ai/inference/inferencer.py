"""Inference engine: preprocessing → forward pass → raw output."""

from __future__ import annotations

import asyncio
import logging
import time
from typing import Optional, Tuple

import numpy as np
import torch
import torch.nn as nn

from app.ai.inference.gpu_utils import select_device
from app.ai.inference.model_loader import get_default_checkpoint_path, load_model
from app.ai.preprocessing.image_processor import preprocess_for_inference
from app.core.config import settings
from app.core.constants import INFERENCE_IMAGE_SIZE, ModelArchitecture
from app.core.exceptions import InferenceError

logger = logging.getLogger(__name__)


class Inferencer:
    """Wraps a loaded model and exposes async inference."""

    def __init__(
        self,
        architecture: ModelArchitecture = ModelArchitecture.UNET,
        checkpoint_path: Optional[str] = None,
        device: Optional[torch.device] = None,
    ) -> None:
        self.architecture = architecture
        self.checkpoint_path = checkpoint_path or get_default_checkpoint_path(architecture)
        self.device = device or select_device(settings.MODEL_DEVICE)
        self._model: Optional[nn.Module] = None

    def _ensure_model(self) -> nn.Module:
        if self._model is None:
            self._model = load_model(
                self.architecture,
                self.checkpoint_path,
                self.device,
            )
        return self._model

    async def infer(self, image: np.ndarray) -> Tuple[np.ndarray, float, float]:
        """Run inference on a preprocessed float32 image array.

        Args:
            image: numpy array, shape (H, W) or (1, H, W) or (H, W, C).

        Returns:
            (probability_map, confidence, inference_time_seconds)
            probability_map: float32 array of shape (H, W) in [0, 1]
        """
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(None, self._infer_sync, image)

    def _infer_sync(self, image: np.ndarray) -> Tuple[np.ndarray, float, float]:
        model = self._ensure_model()

        # Preprocess to (1, H, W)
        processed = preprocess_for_inference(image, INFERENCE_IMAGE_SIZE)

        # Add batch dim → (1, 1, H, W)
        tensor = torch.from_numpy(processed).unsqueeze(0).to(self.device)

        t0 = time.perf_counter()
        try:
            with torch.no_grad():
                # Use FP16 on CUDA for speed
                if self.device.type == "cuda":
                    with torch.autocast(device_type="cuda"):
                        logits = model(tensor)
                else:
                    logits = model(tensor)
        except Exception as exc:
            raise InferenceError(detail=f"Forward pass failed: {exc}") from exc

        elapsed = time.perf_counter() - t0

        # Sigmoid → probability map
        prob = torch.sigmoid(logits).squeeze().cpu().float().numpy()  # (H, W)
        confidence = float(prob.max())

        logger.info(
            "Inference complete arch=%s device=%s time=%.3fs confidence=%.3f",
            self.architecture.value,
            self.device,
            elapsed,
            confidence,
        )
        return prob, confidence, elapsed
