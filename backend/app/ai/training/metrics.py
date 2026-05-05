"""Segmentation metrics for training evaluation."""

from __future__ import annotations

import torch


def dice_coefficient(pred: torch.Tensor, target: torch.Tensor, threshold: float = 0.5, smooth: float = 1.0) -> float:
    """Compute Dice coefficient."""
    pred_bin = (torch.sigmoid(pred) > threshold).float()
    intersection = (pred_bin * target).sum()
    return float((2.0 * intersection + smooth) / (pred_bin.sum() + target.sum() + smooth))


def iou_score(pred: torch.Tensor, target: torch.Tensor, threshold: float = 0.5, smooth: float = 1.0) -> float:
    """Compute Intersection over Union (Jaccard index)."""
    pred_bin = (torch.sigmoid(pred) > threshold).float()
    intersection = (pred_bin * target).sum()
    union = pred_bin.sum() + target.sum() - intersection
    return float((intersection + smooth) / (union + smooth))


def precision_score(pred: torch.Tensor, target: torch.Tensor, threshold: float = 0.5, smooth: float = 1.0) -> float:
    pred_bin = (torch.sigmoid(pred) > threshold).float()
    tp = (pred_bin * target).sum()
    fp = (pred_bin * (1 - target)).sum()
    return float((tp + smooth) / (tp + fp + smooth))


def recall_score(pred: torch.Tensor, target: torch.Tensor, threshold: float = 0.5, smooth: float = 1.0) -> float:
    pred_bin = (torch.sigmoid(pred) > threshold).float()
    tp = (pred_bin * target).sum()
    fn = ((1 - pred_bin) * target).sum()
    return float((tp + smooth) / (tp + fn + smooth))


def f1_score(pred: torch.Tensor, target: torch.Tensor, threshold: float = 0.5) -> float:
    p = precision_score(pred, target, threshold)
    r = recall_score(pred, target, threshold)
    if p + r == 0:
        return 0.0
    return 2 * p * r / (p + r)
