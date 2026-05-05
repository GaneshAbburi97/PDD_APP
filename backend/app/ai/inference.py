"""backend/app/ai/inference.py — Lightweight U-Net inference engine with lazy loading."""

from __future__ import annotations

import logging
import time
from pathlib import Path
from typing import Any, Dict, Optional

import numpy as np

from app.ai.preprocessing import load_and_preprocess
from app.core.config import get_settings

logger = logging.getLogger(__name__)


class LightweightUNet:
    """
    Minimal U-Net for IAN segmentation.

    Architecture:
    - Encoder: 3 × (Conv→BN→ReLU) × 2 + MaxPool
    - Bottleneck: 2 × Conv→BN→ReLU
    - Decoder: 3 × Upsample + skip + (Conv→BN→ReLU) × 2
    - Output: 1 × 1 Conv + Sigmoid

    Parameters: ~2.5 M  |  Compressed checkpoint: ~2.5 MB
    CPU inference: 5–15 s per 512 × 512 image
    """

    def __init__(self) -> None:
        import torch
        import torch.nn as nn

        def _block(in_ch: int, out_ch: int) -> nn.Sequential:
            return nn.Sequential(
                nn.Conv2d(in_ch, out_ch, 3, padding=1, bias=False),
                nn.BatchNorm2d(out_ch),
                nn.ReLU(inplace=True),
                nn.Conv2d(out_ch, out_ch, 3, padding=1, bias=False),
                nn.BatchNorm2d(out_ch),
                nn.ReLU(inplace=True),
            )

        self.enc1 = _block(1, 32)
        self.enc2 = _block(32, 64)
        self.enc3 = _block(64, 128)
        self.pool = nn.MaxPool2d(2)

        self.bottleneck = _block(128, 256)

        self.up3 = nn.ConvTranspose2d(256, 128, 2, stride=2)
        self.dec3 = _block(256, 128)
        self.up2 = nn.ConvTranspose2d(128, 64, 2, stride=2)
        self.dec2 = _block(128, 64)
        self.up1 = nn.ConvTranspose2d(64, 32, 2, stride=2)
        self.dec1 = _block(64, 32)

        self.out = nn.Sequential(
            nn.Conv2d(32, 1, 1),
            nn.Sigmoid(),
        )

    def forward(self, x):
        import torch

        e1 = self.enc1(x)
        e2 = self.enc2(self.pool(e1))
        e3 = self.enc3(self.pool(e2))
        b = self.bottleneck(self.pool(e3))

        d3 = self.dec3(torch.cat([self.up3(b), e3], dim=1))
        d2 = self.dec2(torch.cat([self.up2(d3), e2], dim=1))
        d1 = self.dec1(torch.cat([self.up1(d2), e1], dim=1))

        return self.out(d1)


class InferenceEngine:
    """
    Lazy-loading inference engine with CPU/GPU support.

    The model is loaded from disk on the first call to ``infer()``.
    Subsequent calls reuse the in-memory model.
    """

    def __init__(self) -> None:
        self.settings = get_settings()
        self._model: Optional[Any] = None
        self._device: Optional[Any] = None

    @property
    def is_loaded(self) -> bool:
        return self._model is not None

    def _load_model(self) -> None:
        import torch

        device_str = self.settings.inference_device
        self._device = torch.device(device_str if torch.cuda.is_available() or device_str == "cpu" else "cpu")
        logger.info("Inference device: %s", self._device)

        model = LightweightUNet()

        ckpt_path = Path(self.settings.model_checkpoint_path)
        if ckpt_path.exists():
            state = torch.load(str(ckpt_path), map_location=self._device)
            # Support checkpoints saved with/without the model key
            if "model_state_dict" in state:
                state = state["model_state_dict"]
            model.load_state_dict(state, strict=False)
            logger.info("Loaded checkpoint from %s", ckpt_path)
        else:
            logger.warning(
                "No checkpoint found at %s — running with random weights (research placeholder)",
                ckpt_path,
            )

        model.to(self._device)
        model.eval()
        self._model = model

    def infer(self, file_path: str) -> Dict[str, Any]:
        """
        Run full inference pipeline on a single image file.

        Args:
            file_path: Absolute path to a PNG, JPG, or DICOM file.

        Returns:
            Dictionary with keys:
            - ``mask``: binary segmentation mask as a Python list (H × W)
            - ``confidence``: mean probability in the IAN region
            - ``inference_time_ms``: wall-clock inference time
            - ``output_url``: placeholder URL (set to empty; upload logic can extend this)
        """
        import torch

        if not self.is_loaded:
            self._load_model()

        t0 = time.perf_counter()

        # Preprocessing
        tensor_np = load_and_preprocess(file_path, target_size=(self.settings.model_input_size,) * 2)
        x = torch.from_numpy(tensor_np).to(self._device)

        # Inference
        with torch.no_grad():
            prob = self._model(x)  # (1, 1, H, W) in [0, 1]

        prob_np = prob.squeeze().cpu().numpy()  # (H, W)
        mask = (prob_np > 0.5).astype(np.uint8)

        elapsed_ms = int((time.perf_counter() - t0) * 1000)
        confidence = float(prob_np[mask == 1].mean()) if mask.sum() > 0 else 0.0

        logger.info(
            "Inference complete: file=%s  confidence=%.3f  time=%dms",
            Path(file_path).name,
            confidence,
            elapsed_ms,
        )

        return {
            "mask": mask.tolist(),
            "confidence": confidence,
            "inference_time_ms": elapsed_ms,
            "output_url": "",
        }


# ── Module-level singleton ────────────────────────────────────────────────────

_engine: Optional[InferenceEngine] = None


def get_inference_engine() -> InferenceEngine:
    global _engine
    if _engine is None:
        _engine = InferenceEngine()
    return _engine
