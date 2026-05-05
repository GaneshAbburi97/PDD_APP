"""Tests for inference service integration."""

from __future__ import annotations

import numpy as np
import pytest
from unittest.mock import AsyncMock, MagicMock, patch


@pytest.mark.asyncio
async def test_ai_inference_service_run():
    """AIInferenceService.run should return the expected keys."""
    with patch("app.services.ai_inference_service.Inferencer") as MockInf, \
         patch("app.services.ai_inference_service.load_image") as mock_load:

        mock_load.return_value = (np.random.rand(64, 64).astype(np.float32), {"source": "test"})
        MockInf.return_value.infer = AsyncMock(
            return_value=(np.random.rand(512, 512).astype(np.float32), 0.9, 1.2)
        )

        from app.services.ai_inference_service import AIInferenceService
        svc = AIInferenceService()
        result = await svc.run("/tmp/fake.dcm", "job-1")

    assert "mask_bytes" in result
    assert "overlay_bytes" in result
    assert "heatmap_bytes" in result
    assert "report_bytes" in result
    assert result["confidence"] == pytest.approx(0.9)
