"""Tests for inference pipeline components."""

from __future__ import annotations

import numpy as np
import pytest


def test_unet_forward_pass():
    """U-Net should produce output of the same spatial size as input."""
    try:
        import torch
    except ImportError:
        pytest.skip("PyTorch not installed")

    from app.ai.architectures.unet import UNet

    model = UNet(in_channels=1, num_classes=1)
    model.eval()
    x = torch.randn(1, 1, 128, 128)
    with torch.no_grad():
        out = model(x)
    assert out.shape == (1, 1, 128, 128)


def test_attention_unet_forward_pass():
    try:
        import torch
    except ImportError:
        pytest.skip("PyTorch not installed")

    from app.ai.architectures.attention_unet import AttentionUNet

    model = AttentionUNet(in_channels=1, num_classes=1)
    model.eval()
    x = torch.randn(1, 1, 64, 64)
    with torch.no_grad():
        out = model(x)
    assert out.shape == (1, 1, 64, 64)


def test_deeplab_forward_pass():
    try:
        import torch
    except ImportError:
        pytest.skip("PyTorch not installed")

    from app.ai.architectures.deeplab import DeepLabV3Plus

    model = DeepLabV3Plus(in_channels=1, num_classes=1)
    model.eval()
    x = torch.randn(1, 1, 64, 64)
    with torch.no_grad():
        out = model(x)
    assert out.shape == (1, 1, 64, 64)


def test_model_loader_no_checkpoint():
    """load_model should work without a checkpoint (random weights + warning)."""
    try:
        import torch
    except ImportError:
        pytest.skip("PyTorch not installed")

    from app.ai.inference.model_loader import load_model, clear_model_cache
    from app.core.constants import ModelArchitecture

    clear_model_cache()
    model = load_model(ModelArchitecture.UNET, checkpoint_path=None)
    assert model is not None
    clear_model_cache()
