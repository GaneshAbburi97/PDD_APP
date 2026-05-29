import torch
import torch.nn as nn
import nibabel as nib
import numpy as np
import os
import asyncio
import logging
from typing import Tuple, Dict, Optional
import tempfile

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

async def run_inference(file_url: str, job_id: str) -> Tuple[Dict, str]:
    temp_dir = os.path.join(tempfile.gettempdir(), "medical_processor")
    os.makedirs(temp_dir, exist_ok=True)

    logger.info(f"📊 Processing job {job_id}...")

    try:
        await asyncio.sleep(2)
        logger.info(f"📥 Simulated download from: {file_url}")

        dummy_data = np.random.rand(64, 64, 64).astype(np.float32)
        logger.info(f"🧠 Input volume shape: {dummy_data.shape}")

        model = get_model()

        with torch.no_grad():
            input_slice = dummy_data[0:1, :, :].astype(np.float32)
            input_tensor = torch.from_numpy(input_slice).unsqueeze(0).to(_model_device)

            logger.info(f"🔄 Running inference on {_model_device}...")
            output = model(input_tensor)

            output_np = output.cpu().numpy().squeeze()
            logger.info(f"✅ Inference output shape: {output_np.shape}")

        segmentation_volume = np.sum(dummy_data > 0.5) * 0.001
        result_metadata = {
            "volume_ml": float(segmentation_volume),
            "segmentation_score": 0.89,
            "processing_unit": f"CPU-Research-Mode ({_model_device})",
            "timestamp": int(np.random.random() * 1000)
        }

        logger.info(f"📈 Segmentation stats: {result_metadata}")

        output_path = os.path.join(temp_dir, f"{job_id}_result.nii.gz")
        os.makedirs(os.path.dirname(output_path), exist_ok=True)

        affine = np.eye(4)
        res_img = nib.Nifti1Image(dummy_data.astype(np.float32), affine)
        nib.save(res_img, output_path)

        logger.info(f"💾 Result saved: {output_path}")

        return result_metadata, output_path

    except Exception as e:
        logger.error(f"❌ Inference failed: {e}", exc_info=True)
        raise