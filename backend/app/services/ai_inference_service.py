"""AI inference service: end-to-end pipeline from file path to result artifacts."""

from __future__ import annotations

import logging
import os
from typing import Any, Dict, Optional, Tuple

import numpy as np

from app.ai.inference.inferencer import Inferencer
from app.ai.postprocessing.heatmap_generator import generate_heatmap
from app.ai.postprocessing.mask_processor import mask_to_png_bytes, refine_mask, threshold_mask
from app.ai.postprocessing.overlay_generator import generate_overlay
from app.ai.postprocessing.report_generator import generate_report
from app.ai.preprocessing.dicom_loader import load_image
from app.core.constants import ModelArchitecture

logger = logging.getLogger(__name__)


class AIInferenceService:
    """Orchestrates the full AI inference pipeline."""

    def __init__(
        self,
        architecture: ModelArchitecture = ModelArchitecture.UNET,
        checkpoint_path: Optional[str] = None,
    ) -> None:
        self._inferencer = Inferencer(architecture=architecture, checkpoint_path=checkpoint_path)
        self.architecture = architecture

    async def run(
        self,
        file_path: str,
        job_id: str,
        modality: str = "panoramic",
        metadata: Optional[Dict[str, Any]] = None,
    ) -> Dict[str, Any]:
        """Run the complete inference pipeline.

        Returns a dict with keys:
            mask_bytes, overlay_bytes, heatmap_bytes, report_bytes,
            confidence, inference_time, image_size
        """
        logger.info("Starting inference pipeline job=%s arch=%s", job_id, self.architecture.value)

        # 1. Load image
        image_array, img_metadata = load_image(file_path)
        h = image_array.shape[-2] if image_array.ndim >= 2 else 0
        w = image_array.shape[-1] if image_array.ndim >= 2 else 0

        # 2. Inference
        prob_map, confidence, inference_time = await self._inferencer.infer(image_array)

        # 3. Post-processing
        raw_mask = threshold_mask(prob_map, threshold=0.5)
        refined_mask = refine_mask(raw_mask)

        # 4. Generate artefacts
        mask_bytes = mask_to_png_bytes(refined_mask)
        overlay_bytes = generate_overlay(image_array, refined_mask)
        heatmap_bytes = generate_heatmap(prob_map)

        result_urls: Dict[str, str] = {}  # caller will fill these after upload

        report_bytes = generate_report(
            job_id=job_id,
            mask=refined_mask,
            prob_map=prob_map,
            confidence=confidence,
            inference_time=inference_time,
            model_architecture=self.architecture.value,
            modality=modality,
            image_size=[h, w],
            result_urls=result_urls,
            metadata={**img_metadata, **(metadata or {})},
        )

        logger.info(
            "Inference pipeline complete job=%s confidence=%.3f time=%.3fs",
            job_id, confidence, inference_time,
        )

        return {
            "mask_bytes": mask_bytes,
            "overlay_bytes": overlay_bytes,
            "heatmap_bytes": heatmap_bytes,
            "report_bytes": report_bytes,
            "confidence": confidence,
            "inference_time": inference_time,
            "image_size": [h, w],
        }
