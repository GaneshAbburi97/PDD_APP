"""PyTorch training pipeline for IAN segmentation."""

from __future__ import annotations

import logging
import os
from typing import Any, Dict, Optional

import torch
import torch.nn as nn
from torch.cuda.amp import GradScaler, autocast
from torch.optim import Adam
from torch.optim.lr_scheduler import ReduceLROnPlateau
from torch.utils.data import DataLoader

from app.ai.training.loss_functions import BCEDiceLoss
from app.ai.training.metrics import dice_coefficient, iou_score

logger = logging.getLogger(__name__)


class Trainer:
    """Generic segmentation trainer with mixed-precision, checkpointing, and early stopping."""

    def __init__(
        self,
        model: nn.Module,
        train_loader: DataLoader,
        val_loader: DataLoader,
        checkpoint_dir: str = "/app/checkpoints",
        run_name: str = "run",
        device: Optional[torch.device] = None,
        lr: float = 1e-4,
        max_epochs: int = 100,
        patience: int = 10,
        use_amp: bool = True,
    ) -> None:
        self.model = model
        self.train_loader = train_loader
        self.val_loader = val_loader
        self.checkpoint_dir = checkpoint_dir
        self.run_name = run_name
        self.device = device or torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.max_epochs = max_epochs
        self.patience = patience
        self.use_amp = use_amp and self.device.type == "cuda"

        self.criterion = BCEDiceLoss()
        self.optimizer = Adam(model.parameters(), lr=lr)
        self.scheduler = ReduceLROnPlateau(self.optimizer, patience=5, factor=0.5, verbose=True)
        self.scaler = GradScaler(enabled=self.use_amp)

        self.best_dice = 0.0
        self.epochs_without_improvement = 0

        os.makedirs(checkpoint_dir, exist_ok=True)
        self.model.to(self.device)

    def train_epoch(self) -> Dict[str, float]:
        self.model.train()
        total_loss = 0.0
        for batch in self.train_loader:
            imgs, masks = batch["image"].to(self.device), batch["mask"].to(self.device)
            self.optimizer.zero_grad()
            with autocast(enabled=self.use_amp):
                logits = self.model(imgs)
                loss = self.criterion(logits, masks)
            self.scaler.scale(loss).backward()
            self.scaler.step(self.optimizer)
            self.scaler.update()
            total_loss += loss.item()
        return {"loss": total_loss / max(len(self.train_loader), 1)}

    def validate_epoch(self) -> Dict[str, float]:
        self.model.eval()
        total_loss = 0.0
        total_dice = 0.0
        total_iou = 0.0
        n = len(self.val_loader)
        with torch.no_grad():
            for batch in self.val_loader:
                imgs, masks = batch["image"].to(self.device), batch["mask"].to(self.device)
                logits = self.model(imgs)
                loss = self.criterion(logits, masks)
                total_loss += loss.item()
                total_dice += dice_coefficient(logits, masks)
                total_iou += iou_score(logits, masks)
        return {
            "val_loss": total_loss / max(n, 1),
            "dice": total_dice / max(n, 1),
            "iou": total_iou / max(n, 1),
        }

    def fit(self) -> Dict[str, Any]:
        history: Dict[str, Any] = {"epochs": []}
        for epoch in range(1, self.max_epochs + 1):
            train_metrics = self.train_epoch()
            val_metrics = self.validate_epoch()
            self.scheduler.step(val_metrics["val_loss"])

            log_line = f"Epoch {epoch}/{self.max_epochs} | " + " | ".join(
                f"{k}={v:.4f}" for k, v in {**train_metrics, **val_metrics}.items()
            )
            logger.info(log_line)
            history["epochs"].append({**train_metrics, **val_metrics, "epoch": epoch})

            # Checkpoint
            if val_metrics["dice"] > self.best_dice:
                self.best_dice = val_metrics["dice"]
                self.epochs_without_improvement = 0
                self._save_checkpoint(epoch, val_metrics["dice"], is_best=True)
            else:
                self.epochs_without_improvement += 1
                if self.epochs_without_improvement >= self.patience:
                    logger.info("Early stopping at epoch %d", epoch)
                    break

        history["best_dice"] = self.best_dice
        return history

    def _save_checkpoint(self, epoch: int, dice: float, is_best: bool = False) -> str:
        path = os.path.join(self.checkpoint_dir, f"{self.run_name}_epoch{epoch}_dice{dice:.4f}.pth")
        torch.save(
            {
                "epoch": epoch,
                "model_state_dict": self.model.state_dict(),
                "optimizer_state_dict": self.optimizer.state_dict(),
                "dice": dice,
            },
            path,
        )
        if is_best:
            import shutil
            best_path = os.path.join(self.checkpoint_dir, f"{self.run_name}_best.pth")
            shutil.copy2(path, best_path)
            logger.info("Saved best checkpoint (dice=%.4f): %s", dice, best_path)
        return path
