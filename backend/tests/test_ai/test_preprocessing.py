"""Tests for AI preprocessing pipeline."""

from __future__ import annotations

import numpy as np
import pytest


def test_preprocess_for_inference_grayscale():
    from app.ai.preprocessing.image_processor import preprocess_for_inference
    img = np.random.rand(512, 512).astype(np.float32)
    out = preprocess_for_inference(img)
    assert out.shape == (1, 512, 512)
    assert out.dtype == np.float32


def test_preprocess_for_inference_rgb():
    from app.ai.preprocessing.image_processor import preprocess_for_inference
    img = np.random.rand(256, 256, 3).astype(np.float32)
    out = preprocess_for_inference(img)
    assert out.shape == (1, 512, 512)


def test_resize_image():
    from app.ai.preprocessing.image_processor import resize_image
    img = np.zeros((100, 100), dtype=np.float32)
    resized = resize_image(img, (512, 512))
    assert resized.shape == (512, 512)


def test_min_max_normalise():
    from app.ai.preprocessing.normalizer import min_max_normalise
    arr = np.array([0.0, 50.0, 100.0], dtype=np.float32)
    out = min_max_normalise(arr)
    assert abs(out.min()) < 1e-5
    assert abs(out.max() - 1.0) < 1e-5


def test_threshold_mask():
    from app.ai.postprocessing.mask_processor import threshold_mask
    prob = np.array([[0.2, 0.8], [0.6, 0.1]])
    mask = threshold_mask(prob, threshold=0.5)
    assert mask[0, 0] == 0
    assert mask[0, 1] == 1
    assert mask.dtype == np.uint8


def test_mask_to_png_bytes():
    from app.ai.postprocessing.mask_processor import mask_to_png_bytes
    mask = np.zeros((64, 64), dtype=np.uint8)
    mask[10:20, 10:20] = 255
    data = mask_to_png_bytes(mask)
    assert isinstance(data, bytes)
    assert data[:4] == b"\x89PNG"


def test_heatmap_generation():
    from app.ai.postprocessing.heatmap_generator import generate_heatmap
    prob = np.random.rand(64, 64).astype(np.float32)
    data = generate_heatmap(prob)
    assert isinstance(data, bytes)
    assert len(data) > 100
