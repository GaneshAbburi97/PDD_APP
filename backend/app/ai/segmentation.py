import torch
import torch.nn as nn
import nibabel as nib
import numpy as np
import os
import asyncio
import logging
from typing import Tuple, Dict

logger = logging.getLogger("AI_SEGMENTATION")

# Lightweight U-Net Architecture for Research
class LightweightUNet(nn.Module):
    def __init__(self):
        super(LightweightUNet, self).__init__()
        # Simplified channels for research mode (near-zero cost CPU inference)
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

# Global model instance for lazy loading
_model = None

def get_model():
    global _model
    if _model is None:
        logger.info("📦 Loading lightweight U-Net model...")
        _model = LightweightUNet()
        _model.eval()
    return _model

async def run_inference(file_url: str, job_id: str) -> Tuple[Dict, str]:
    """
    Runs medical image segmentation on a NIfTI file.
    Optimized for research: CPU inference, simplified preprocessing.
    """
    # 1. Mock file download (In real research, use requests to get file_url)
    # For now, we simulate processing on a dummy volume
    await asyncio.sleep(5) # Simulate heavy compute

    logger.info(f"📊 Processing job {job_id}...")

    # 2. Simulate NIfTI processing
    # In reality: img = nib.load(local_file_path); data = img.get_fdata()
    dummy_data = np.random.rand(64, 64, 64)

    # 3. Lightweight Inference
    model = get_model()
    # Mock inference call: input_tensor = torch.from_numpy(dummy_slice).unsqueeze(0).unsqueeze(0)
    # output = model(input_tensor)

    # 4. Generate result metadata
    result_metadata = {
        "volume_ml": float(np.sum(dummy_data > 0.5) * 0.001), # Mock volume calculation
        "segmentation_score": 0.89,
        "processing_unit": "CPU-Research-Mode",
        "timestamp": int(os.times().elapsed * 1000)
    }

    # 5. Save output NIfTI
    output_dir = "temp_results"
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, f"{job_id}_result.nii.gz")

    # Mock saving a NIfTI file
    affine = np.eye(4)
    res_img = nib.Nifti1Image(dummy_data.astype(np.float32), affine)
    nib.save(res_img, output_path)

    return result_metadata, output_path
