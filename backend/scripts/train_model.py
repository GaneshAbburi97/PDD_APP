"""Training script for IAN segmentation model."""

from __future__ import annotations

import argparse
import glob
import logging
import os
import sys

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def parse_args():
    parser = argparse.ArgumentParser(description="Train IAN segmentation model")
    parser.add_argument("--arch", default="unet", choices=["unet", "attention_unet", "deeplab"])
    parser.add_argument("--image-dir", required=True, help="Directory containing training images")
    parser.add_argument("--mask-dir", required=True, help="Directory containing training masks")
    parser.add_argument("--checkpoint-dir", default="/app/checkpoints")
    parser.add_argument("--epochs", type=int, default=100)
    parser.add_argument("--batch-size", type=int, default=4)
    parser.add_argument("--lr", type=float, default=1e-4)
    parser.add_argument("--image-size", type=int, default=512)
    parser.add_argument("--val-split", type=float, default=0.2)
    parser.add_argument("--patience", type=int, default=10)
    return parser.parse_args()


def main() -> None:
    args = parse_args()

    try:
        import torch
        from torch.utils.data import DataLoader, random_split
    except ImportError:
        logger.error("PyTorch is required for training. pip install torch")
        sys.exit(1)

    from app.ai.architectures.unet import UNet
    from app.ai.architectures.attention_unet import AttentionUNet
    from app.ai.architectures.deeplab import DeepLabV3Plus
    from app.ai.datasets.medical_dataset import MedicalSegmentationDataset
    from app.ai.preprocessing.augmentation import get_train_augmentation, get_val_augmentation
    from app.ai.training.trainer import Trainer

    # Collect image/mask paths
    exts = ("*.dcm", "*.png", "*.jpg", "*.jpeg")
    image_paths = sorted(p for ext in exts for p in glob.glob(os.path.join(args.image_dir, ext)))
    mask_paths = sorted(p for ext in exts for p in glob.glob(os.path.join(args.mask_dir, ext)))

    if not image_paths:
        logger.error("No images found in %s", args.image_dir)
        sys.exit(1)

    if len(image_paths) != len(mask_paths):
        logger.error("Image/mask count mismatch: %d vs %d", len(image_paths), len(mask_paths))
        sys.exit(1)

    logger.info("Found %d image/mask pairs", len(image_paths))

    # Split train/val
    n_val = max(1, int(len(image_paths) * args.val_split))
    n_train = len(image_paths) - n_val

    train_dataset = MedicalSegmentationDataset(
        image_paths[:n_train], mask_paths[:n_train],
        image_size=(args.image_size, args.image_size),
        augmentation=get_train_augmentation(args.image_size),
    )
    val_dataset = MedicalSegmentationDataset(
        image_paths[n_train:], mask_paths[n_train:],
        image_size=(args.image_size, args.image_size),
        augmentation=get_val_augmentation(args.image_size),
    )

    train_loader = DataLoader(train_dataset, batch_size=args.batch_size, shuffle=True, num_workers=4, pin_memory=True)
    val_loader = DataLoader(val_dataset, batch_size=args.batch_size, shuffle=False, num_workers=2)

    # Build model
    arch_map = {"unet": UNet, "attention_unet": AttentionUNet, "deeplab": DeepLabV3Plus}
    model = arch_map[args.arch](in_channels=1, num_classes=1)

    trainer = Trainer(
        model=model,
        train_loader=train_loader,
        val_loader=val_loader,
        checkpoint_dir=args.checkpoint_dir,
        run_name=args.arch,
        lr=args.lr,
        max_epochs=args.epochs,
        patience=args.patience,
    )
    history = trainer.fit()
    logger.info("Training complete. Best Dice: %.4f", history["best_dice"])


if __name__ == "__main__":
    main()
