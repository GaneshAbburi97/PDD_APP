"""Pydantic schemas for AI inference results."""

from __future__ import annotations

from datetime import datetime
from typing import Any, Dict, List, Optional

from pydantic import BaseModel, Field


class SegmentationResult(BaseModel):
    mask_url: Optional[str] = None
    overlay_url: Optional[str] = None
    heatmap_url: Optional[str] = None
    report_url: Optional[str] = None
    dice_score: Optional[float] = Field(default=None, ge=0.0, le=1.0)
    iou_score: Optional[float] = Field(default=None, ge=0.0, le=1.0)
    confidence: Optional[float] = Field(default=None, ge=0.0, le=1.0)
    inference_time_seconds: Optional[float] = None
    model_version: Optional[str] = None


class ProcessingReport(BaseModel):
    job_id: str
    patient_id: Optional[str] = None
    modality: Optional[str] = None
    image_size: Optional[List[int]] = None
    preprocessing_steps: List[str] = []
    inference_device: Optional[str] = None
    model_architecture: Optional[str] = None
    segmentation: Optional[SegmentationResult] = None
    metadata: Optional[Dict[str, Any]] = None
    generated_at: datetime = Field(default_factory=datetime.utcnow)
