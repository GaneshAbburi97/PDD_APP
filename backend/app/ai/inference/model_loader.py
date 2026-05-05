"""Model loader with lazy loading and in-memory caching."""

from __future__ import annotations

import logging
import os
from typing import Dict, Optional

import torch
import torch.nn as nn

from app.ai.architectures.attention_unet import AttentionUNet
from app.ai.architectures.deeplab import DeepLabV3Plus
from app.ai.architectures.unet import UNet
from app.ai.inference.gpu_utils import select_device
from app.core.config import settings
from app.core.constants import ModelArchitecture
from app.core.exceptions import ModelLoadError, ModelNotFoundError

logger = logging.getLogger(__name__)

_MODEL_CACHE: Dict[str, nn.Module] = {}


def _build_model(architecture: ModelArchitecture) -> nn.Module:
    """Instantiate a model by architecture name."""
    if architecture == ModelArchitecture.UNET:
        return UNet(in_channels=1, num_classes=1)
    if architecture == ModelArchitecture.ATTENTION_UNET:
        return AttentionUNet(in_channels=1, num_classes=1)
    if architecture == ModelArchitecture.DEEPLAB:
        return DeepLabV3Plus(in_channels=1, num_classes=1)
    raise ModelLoadError(detail=f"Unknown architecture: {architecture}")


def load_model(
    architecture: ModelArchitecture = ModelArchitecture.UNET,
    checkpoint_path: Optional[str] = None,
    device: Optional[torch.device] = None,
    force_reload: bool = False,
) -> nn.Module:
    """Load (or retrieve from cache) a segmentation model.

    If no checkpoint is found the model is returned in random-weight evaluation
    mode with a warning – useful for smoke-testing the pipeline before training.
    """
    cache_key = f"{architecture.value}:{checkpoint_path}"

    if not force_reload and cache_key in _MODEL_CACHE:
        logger.debug("Model cache hit for %s", cache_key)
        return _MODEL_CACHE[cache_key]

    if device is None:
        device = select_device(settings.MODEL_DEVICE)

    model = _build_model(architecture)

    if checkpoint_path and os.path.isfile(checkpoint_path):
        try:
            state = torch.load(checkpoint_path, map_location=device)
            # Support checkpoints saved as {"model_state_dict": ...}
            if isinstance(state, dict) and "model_state_dict" in state:
                state = state["model_state_dict"]
            model.load_state_dict(state)
            logger.info("Loaded checkpoint: %s", checkpoint_path)
        except Exception as exc:
            raise ModelLoadError(detail=f"Failed to load checkpoint {checkpoint_path}: {exc}") from exc
    else:
        logger.warning(
            "No checkpoint found at '%s'. Using random weights (inference results will be meaningless).",
            checkpoint_path,
        )

    model = model.to(device)
    model.eval()
    _MODEL_CACHE[cache_key] = model
    return model


def get_default_checkpoint_path(architecture: ModelArchitecture) -> str:
    return os.path.join(settings.MODEL_CHECKPOINT_DIR, f"{architecture.value}_best.pth")


def clear_model_cache() -> None:
    _MODEL_CACHE.clear()
    logger.info("Model cache cleared.")
