"""Script to download or register pre-trained model checkpoints."""

from __future__ import annotations

import argparse
import logging
import os

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

PRETRAINED_REGISTRY = {
    # Add real download URLs when weights are available
    # "unet": "https://your-bucket/checkpoints/unet_best.pth",
    # "attention_unet": "https://your-bucket/checkpoints/attention_unet_best.pth",
}


def download_model(arch: str, checkpoint_dir: str) -> None:
    url = PRETRAINED_REGISTRY.get(arch)
    if not url:
        logger.warning("No pre-trained weights registered for architecture '%s'.", arch)
        logger.info("Train a model first using: python scripts/train_model.py --arch %s", arch)
        return

    import urllib.request
    os.makedirs(checkpoint_dir, exist_ok=True)
    dest = os.path.join(checkpoint_dir, f"{arch}_best.pth")
    logger.info("Downloading %s -> %s", url, dest)
    urllib.request.urlretrieve(url, dest)
    logger.info("Downloaded %s", dest)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Download pre-trained model weights")
    parser.add_argument("--arch", default="unet", choices=["unet", "attention_unet", "deeplab"])
    parser.add_argument("--checkpoint-dir", default="/app/checkpoints")
    args = parser.parse_args()
    download_model(args.arch, args.checkpoint_dir)
