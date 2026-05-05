"""ONNX export utility."""

from __future__ import annotations

import logging
import os
from typing import Optional, Tuple

import torch
import torch.nn as nn

logger = logging.getLogger(__name__)


def export_to_onnx(
    model: nn.Module,
    output_path: str,
    input_shape: Tuple[int, ...] = (1, 1, 512, 512),
    opset_version: int = 17,
    device: Optional[torch.device] = None,
) -> str:
    """Export a PyTorch model to ONNX format.

    Args:
        model: Trained PyTorch model (eval mode).
        output_path: Path to save .onnx file.
        input_shape: Input tensor shape (N, C, H, W).
        opset_version: ONNX opset version.
        device: Device for dummy input.

    Returns:
        Path to the exported ONNX file.
    """
    if device is None:
        device = torch.device("cpu")

    os.makedirs(os.path.dirname(output_path) or ".", exist_ok=True)
    model = model.to(device).eval()
    dummy_input = torch.randn(*input_shape, device=device)

    torch.onnx.export(
        model,
        dummy_input,
        output_path,
        opset_version=opset_version,
        input_names=["input"],
        output_names=["output"],
        dynamic_axes={"input": {0: "batch_size"}, "output": {0: "batch_size"}},
        do_constant_folding=True,
    )
    logger.info("Exported model to ONNX: %s", output_path)
    return output_path
