import asyncio
from .segmentation import run_inference as run_segmentation_inference

def run_inference(input_path: str, job_id: str):
    return asyncio.run(run_segmentation_inference(input_path, job_id))
