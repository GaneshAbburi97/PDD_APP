"""Tests for postprocessing components."""

from __future__ import annotations

import numpy as np
import pytest


def test_overlay_generation():
    from app.ai.postprocessing.overlay_generator import generate_overlay

    original = np.random.rand(64, 64).astype(np.float32)
    mask = np.zeros((64, 64), dtype=np.uint8)
    mask[20:40, 20:40] = 255

    result = generate_overlay(original, mask)
    assert isinstance(result, bytes)
    # PNG header
    assert result[:4] == b"\x89PNG"


def test_report_generation():
    from app.ai.postprocessing.report_generator import generate_report

    mask = np.zeros((64, 64), dtype=np.uint8)
    mask[10:20, 10:20] = 255
    prob = np.random.rand(64, 64).astype(np.float32)

    data = generate_report(
        job_id="test-job",
        mask=mask,
        prob_map=prob,
        confidence=0.85,
        inference_time=1.5,
        model_architecture="unet",
        modality="panoramic",
        image_size=[64, 64],
    )
    assert isinstance(data, bytes)
    import json
    report = json.loads(data)
    assert report["job_id"] == "test-job"
    assert "segmentation" in report
