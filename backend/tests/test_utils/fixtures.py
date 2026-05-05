"""Shared test fixtures."""

from __future__ import annotations

import io
import numpy as np
import pytest


@pytest.fixture()
def sample_image_array():
    """512x512 random float32 image in [0,1]."""
    return np.random.rand(512, 512).astype(np.float32)


@pytest.fixture()
def sample_dicom_bytes():
    """Return a minimal DICOM-like bytes object (magic bytes only, not a real DICOM)."""
    header = b"\x00" * 128 + b"DICM"
    return header + b"\x00" * 1024


@pytest.fixture()
def sample_png_bytes():
    """Return real PNG bytes (1x1 white pixel)."""
    from PIL import Image
    img = Image.new("L", (1, 1), 255)
    buf = io.BytesIO()
    img.save(buf, format="PNG")
    return buf.getvalue()
