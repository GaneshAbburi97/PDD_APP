import torch
import torch.nn as nn
import nibabel as nib
import numpy as np
import os
import asyncio
import logging
from typing import Tuple, Dict, Optional
import tempfile
import time

logger = logging.getLogger("AI_SEGMENTATION")

class LightweightUNet(nn.Module):
    def __init__(self):
        super(LightweightUNet, self).__init__()
        self.encoder = nn.Sequential(
            nn.Conv2d(1, 16, 3, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2)
        )
        self.decoder = nn.Sequential(
            nn.ConvTranspose2d(16, 16, 2, stride=2),
            nn.Conv2d(16, 1, 3, padding=1),
            nn.Sigmoid()
        )

    def forward(self, x):
        x = self.encoder(x)
        x = self.decoder(x)
        return x

_model: Optional[LightweightUNet] = None
_model_device = "cpu"

def get_model_device() -> str:
    if torch.cuda.is_available():
        logger.info("🎮 GPU (CUDA) available for inference")
        return "cuda"
    else:
        logger.info("💻 Using CPU for inference (GPU not available)")
        return "cpu"

def get_model() -> LightweightUNet:
    global _model, _model_device
    if _model is None:
        logger.info("📦 Loading lightweight U-Net model...")
        _model = LightweightUNet()
        _model_device = get_model_device()
        _model.to(_model_device)
        _model.eval()
        logger.info(f"✅ Model loaded on device: {_model_device}")
    return _model

def cleanup_model():
    global _model
    if _model is not None:
        logger.info("🧹 Unloading AI model...")
        _model = None

def _normalize_volume(volume: np.ndarray) -> np.ndarray:
    finite_volume = np.nan_to_num(volume.astype(np.float32), nan=0.0, posinf=0.0, neginf=0.0)
    min_val = np.min(finite_volume)
    max_val = np.max(finite_volume)
    if max_val <= min_val:
        return np.zeros_like(finite_volume, dtype=np.float32)
    return (finite_volume - min_val) / (max_val - min_val)

async def run_inference(input_file_path: str, job_id: str) -> Tuple[Dict, str]:
    temp_dir = os.path.join(tempfile.gettempdir(), "medical_processor")
    os.makedirs(temp_dir, exist_ok=True)

    logger.info(f"📊 Processing job {job_id}...")

    try:
        await asyncio.sleep(0)
        nifti = nib.load(input_file_path)
        raw_volume = nifti.get_fdata(dtype=np.float32)
        if raw_volume.ndim == 4:
            logger.info(f"ℹ️ 4D volume detected ({raw_volume.shape}); using first volume/timepoint for segmentation")
            raw_volume = raw_volume[..., 0]
        if raw_volume.ndim != 3:
            raise ValueError("Expected a 3D NIfTI volume for segmentation.")
        logger.info(f"📥 Loaded input volume from: {input_file_path}, shape={raw_volume.shape}")

        normalized_volume = _normalize_volume(raw_volume)
        depth = normalized_volume.shape[2]

        model = get_model()
        mask_slices = []
        confidence_scores = []

        with torch.no_grad():
            logger.info(f"🔄 Running slice inference on {_model_device}...")
            for slice_idx in range(depth):
                input_slice = normalized_volume[:, :, slice_idx]
                input_tensor = torch.from_numpy(input_slice).unsqueeze(0).unsqueeze(0).to(_model_device)
                output = model(input_tensor)
                output_np = output.cpu().numpy().squeeze().astype(np.float32)
                mask_slices.append((output_np > 0.5).astype(np.float32))
                confidence_scores.append(float(output_np.mean()))

        segmentation_mask = np.stack(mask_slices, axis=2).astype(np.float32)
        voxel_sizes = nifti.header.get_zooms()[:3]
        voxel_volume_mm3 = float(voxel_sizes[0] * voxel_sizes[1] * voxel_sizes[2])
        segmentation_volume_ml = float(segmentation_mask.sum() * voxel_volume_mm3 / 1000.0)
        result_metadata = {
            "volume_ml": float(segmentation_volume_ml),
            "segmentation_score": float(np.mean(confidence_scores)) if confidence_scores else 0.0,
            "processing_unit": f"CPU-Research-Mode ({_model_device})",
            "timestamp": int(time.time())
        }

        logger.info(f"📈 Segmentation stats: {result_metadata}")

        output_path = os.path.join(temp_dir, f"{job_id}_result.nii.gz")
        os.makedirs(os.path.dirname(output_path), exist_ok=True)

        res_img = nib.Nifti1Image(segmentation_mask, nifti.affine, nifti.header)
        nib.save(res_img, output_path)

        logger.info(f"💾 Result saved: {output_path}")

        return result_metadata, output_path

    except Exception as e:
        logger.error(f"❌ Inference failed: {e}", exc_info=True)
        raise