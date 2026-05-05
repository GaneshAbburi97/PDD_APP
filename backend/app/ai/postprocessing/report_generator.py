"""JSON report generator for inference results."""

from __future__ import annotations

import json
from datetime import datetime
from typing import Any, Dict, List, Optional

import numpy as np


def compute_mask_stats(mask: np.ndarray) -> Dict[str, Any]:
    """Compute basic statistics for a binary mask."""
    binary = mask > 0
    total_pixels = int(binary.size)
    positive_pixels = int(binary.sum())
    coverage = positive_pixels / total_pixels if total_pixels > 0 else 0.0
    return {
        "total_pixels": total_pixels,
        "positive_pixels": positive_pixels,
        "coverage_ratio": round(coverage, 6),
    }


def generate_report(
    job_id: str,
    mask: np.ndarray,
    prob_map: np.ndarray,
    confidence: float,
    inference_time: float,
    model_architecture: str,
    modality: str,
    image_size: List[int],
    result_urls: Optional[Dict[str, str]] = None,
    metadata: Optional[Dict[str, Any]] = None,
) -> bytes:
    """Generate a JSON report and return as bytes."""
    report = {
        "job_id": job_id,
        "generated_at": datetime.utcnow().isoformat() + "Z",
        "model": {
            "architecture": model_architecture,
            "inference_time_seconds": round(inference_time, 4),
            "device": "gpu" if inference_time < 5.0 else "cpu",
        },
        "image": {
            "modality": modality,
            "size": image_size,
        },
        "segmentation": {
            "confidence": round(confidence, 4),
            "threshold": 0.5,
            "mask_stats": compute_mask_stats(mask),
        },
        "result_urls": result_urls or {},
        "metadata": metadata or {},
    }
    return json.dumps(report, indent=2, default=str).encode("utf-8")
