import logging
import os
import time
import uuid
from typing import List

from fastapi import FastAPI, File, UploadFile, HTTPException, Depends, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

from .utils.firebase import verify_firebase_token, update_job_status, upload_result_to_storage
from .ai.segmentation import run_inference

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("RESEARCH_BACKEND")

app = FastAPI(title="Medical File Processor - Research Mode")

# CORS for local development
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class ProcessRequest(BaseModel):
    fileUrl: str
    fileName: str
    fileSize: int
    cloudProvider: str
    userEmail: str

class ApiResponse(BaseModel):
    success: bool
    message: str
    data: dict = None

@app.get("/health")
async def check_health():
    return {
        "success": True,
        "message": "Backend is online (Research Mode)",
        "data": {"status": "healthy", "version": "1.0.0-research"}
    }

@app.post("/process")
async def start_processing(
    request: ProcessRequest,
    background_tasks: BackgroundTasks,
    user=Depends(verify_firebase_token)
):
    job_id = str(uuid.uuid4())
    logger.info(f"🚀 Starting processing for job: {job_id}")

    # Initialize job in Firestore
    update_job_status(job_id, "QUEUED", 0, user_id=user["uid"], file_name=request.fileName)

    # Add background task for AI inference
    background_tasks.add_task(
        process_job,
        job_id,
        request.fileUrl,
        request.fileName,
        user["uid"]
    )

    return {
        "success": True,
        "message": "Processing started",
        "data": {
            "jobId": job_id,
            "estimatedTimeSeconds": 120,
            "status": "QUEUED"
        }
    }

async def process_job(job_id: str, file_url: str, file_name: str, user_id: str):
    try:
        # 1. Update status to PROCESSING
        update_job_status(job_id, "PROCESSING", 10)

        # 2. Run AI Inference (Lightweight U-Net)
        # In research mode, this runs locally on CPU
        logger.info(f"🧠 Running inference for {job_id}...")
        result_data, output_file_path = await run_inference(file_url, job_id)

        update_job_status(job_id, "PROCESSING", 80)

        # 3. Upload result to Firebase Storage
        result_url = upload_result_to_storage(output_file_path, f"results/{user_id}/{job_id}/output.nii.gz")

        # 4. Final update to Firestore
        update_job_status(
            job_id,
            "COMPLETED",
            100,
            result_url=result_url,
            metadata=result_data
        )
        logger.info(f"✅ Job {job_id} completed successfully")

    except Exception as e:
        logger.error(f"❌ Job {job_id} failed: {str(e)}")
        update_job_status(job_id, "FAILED", 0, error_message=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
