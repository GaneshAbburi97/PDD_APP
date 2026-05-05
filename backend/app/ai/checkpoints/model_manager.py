"""Checkpoint manager: versioning, best-model tracking, rollback."""

from __future__ import annotations

import logging
import os
import shutil
from typing import Any, Dict, List, Optional

import torch

logger = logging.getLogger(__name__)


class ModelManager:
    def __init__(self, checkpoint_dir: str) -> None:
        self.checkpoint_dir = checkpoint_dir
        os.makedirs(checkpoint_dir, exist_ok=True)

    def list_checkpoints(self, architecture: Optional[str] = None) -> List[str]:
        """List all .pth files in checkpoint_dir, optionally filtered by architecture."""
        files = [f for f in os.listdir(self.checkpoint_dir) if f.endswith(".pth")]
        if architecture:
            files = [f for f in files if architecture in f]
        return sorted(files)

    def get_best_checkpoint(self, architecture: str) -> Optional[str]:
        """Return path to <architecture>_best.pth or None."""
        path = os.path.join(self.checkpoint_dir, f"{architecture}_best.pth")
        return path if os.path.isfile(path) else None

    def promote_to_best(self, checkpoint_path: str, architecture: str) -> str:
        """Copy checkpoint_path to <arch>_best.pth."""
        dest = os.path.join(self.checkpoint_dir, f"{architecture}_best.pth")
        shutil.copy2(checkpoint_path, dest)
        logger.info("Promoted %s -> %s", checkpoint_path, dest)
        return dest

    def load_metadata(self, checkpoint_path: str) -> Dict[str, Any]:
        """Load checkpoint and return metadata (epoch, metrics) without loading model weights."""
        ckpt = torch.load(checkpoint_path, map_location="cpu")
        return {k: v for k, v in ckpt.items() if k != "model_state_dict" and k != "optimizer_state_dict"}

    def delete_checkpoint(self, checkpoint_path: str) -> None:
        if os.path.isfile(checkpoint_path):
            os.remove(checkpoint_path)
            logger.info("Deleted checkpoint: %s", checkpoint_path)
