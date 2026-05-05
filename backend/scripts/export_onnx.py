"""Export a trained PyTorch model to ONNX format."""

from __future__ import annotations

import argparse
import logging
import sys

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def parse_args():
    parser = argparse.ArgumentParser(description="Export model to ONNX")
    parser.add_argument("--arch", default="unet", choices=["unet", "attention_unet", "deeplab"])
    parser.add_argument("--checkpoint", required=True, help="Path to .pth checkpoint")
    parser.add_argument("--output", required=True, help="Output .onnx path")
    parser.add_argument("--image-size", type=int, default=512)
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    try:
        import torch
    except ImportError:
        logger.error("PyTorch required. pip install torch")
        sys.exit(1)

    from app.ai.architectures.unet import UNet
    from app.ai.architectures.attention_unet import AttentionUNet
    from app.ai.architectures.deeplab import DeepLabV3Plus
    from app.ai.inference.model_loader import load_model
    from app.exports.onnx_exporter import export_to_onnx
    from app.core.constants import ModelArchitecture

    arch_map = {
        "unet": ModelArchitecture.UNET,
        "attention_unet": ModelArchitecture.ATTENTION_UNET,
        "deeplab": ModelArchitecture.DEEPLAB,
    }
    arch = arch_map[args.arch]
    model = load_model(arch, checkpoint_path=args.checkpoint)

    export_to_onnx(
        model,
        args.output,
        input_shape=(1, 1, args.image_size, args.image_size),
    )
    logger.info("Exported to %s", args.output)


if __name__ == "__main__":
    main()
