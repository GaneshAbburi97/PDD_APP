import asyncio
import logging
import os
import shutil
import tempfile
import time
import uuid
from contextlib import asynccontextmanager
from typing import List

from fastapi import FastAPI, File, UploadFile, HTTPException, Depends, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

from .utils.firebase import verify_firebase_token, update_job_status, upload_result_to_storage
from .ai.segmentation import run_inference, cleanup_model

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='[%(asctime)s] %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger("RESEARCH_BACKEND")

# ===== Firebase Admin Singleton =====
# Initialized on startup, cleaned up on shutdown
firebase_initialized = False

@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Startup/shutdown events:
    - Initialize Firebase Admin SDK on startup
    - Clean up temp files on shutdown
    - Cleanup AI models on shutdown
    """
    global firebase_initialized

    # STARTUP
    logger.info("🚀 Starting Medical Processor Backend (Research Mode)")
    try:
        from .utils.firebase import db, bucket
        firebase_initialized = True
        logger.info("✅ Firebase initialized")
    except Exception as e:
        logger.error(f"❌ Firebase init failed: {e}")
        firebase_initialized = False

    # Create temp directory for processing
    temp_dir = os.path.join(tempfile.gettempdir(), "medical_processor")
    os.makedirs(temp_dir, exist_ok=True)
    logger.info(f"📁 Temp directory: {temp_dir}")

    yield

    # SHUTDOWN
    logger.info("🛑 Shutting down Medical Processor Backend")

    # Cleanup temp files
    try:
        if os.path.exists(temp_dir):
            shutil.rmtree(temp_dir, ignore_errors=True)
            logger.info(f"🧹 Cleaned temp directory")
    except Exception as e:
        logger.error(f"⚠️ Error cleaning temp directory: {e}")

    # Cleanup AI models
    try:
        cleanup_model()
        logger.info("🧹 AI models unloaded")
    except Exception as e:
        logger.error(f"⚠️ Error cleaning AI models: {e}")

    logger.info("✅ Shutdown complete")

# Create FastAPI app with lifespan
app = FastAPI(
    title="Medical File Processor - Research Mode",
    description="FastAPI backend for medical file processing with Firebase integration",
    version="1.0.0-research",
    lifespan=lifespan
)

# CORS for local development
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ===== Request/Response Models =====

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

# ===== Health Check =====

@app.get("/health")
async def check_health():
    """Health check endpoint - no auth required."""
    return {
        "success": True,
        "message": "Backend is online (Research Mode)",
        "data": {
            "status": "healthy",
            "version": "1.0.0-research",
            "firebase": "initialized" if firebase_initialized else "not initialized"
        }
    }

# ===== Main Processing Endpoint =====

@app.post("/process")
async def start_processing(
    request: ProcessRequest,
    background_tasks: BackgroundTasks,
    user=Depends(verify_firebase_token)
):
    """
    Start processing a medical file.

    SAFETY:
    - Background task isolated for AI inference (async isolation)
    - Job status tracked in Firestore
    - Automatic cleanup of temp files
    """
    if not firebase_initialized:
        raise HTTPException(status_code=503, detail="Firebase not initialized")

    job_id = str(uuid.uuid4())
    user_id = user.get("uid", "unknown") if isinstance(user, dict) else user.uid

    logger.info(f"🚀 Starting processing for job: {job_id} (user: {user_id})")

    try:
        # Initialize job in Firestore
        update_job_status(
            job_id,
            "QUEUED",
            0,
            user_id=user_id,
            file_name=request.fileName
        )
        logger.info(f"✅ Job created in Firestore: {job_id}")
    except Exception as e:
        logger.error(f"❌ Failed to create job in Firestore: {e}")
        raise HTTPException(status_code=500, detail="Failed to initialize job")

    # Add background task for AI inference
    # Using add_task ensures it runs after response is sent
    background_tasks.add_task(
        process_job,
        job_id,
        request.fileUrl,
        request.fileName,
        user_id
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

# ===== Background Processing Task =====

async def process_job(job_id: str, file_url: str, file_name: str, user_id: str):
    """
    Background processing task for AI inference.

    SAFETY:
    - Runs asynchronously (doesn't block request thread)
    - AI inference isolated via asyncio (CPU-intensive work in executor)
    - Firestore updates for progress tracking
    - Exception handling prevents orphan jobs
    - Temp files cleaned up on completion/failure
    """
    temp_output_file = None

    try:
        # 1. Update status to PROCESSING
        update_job_status(job_id, "PROCESSING", 10)
        logger.info(f"📊 Job {job_id} moved to PROCESSING")

        # 2. Run AI Inference with CPU isolation
        # Blocking CPU work in executor prevents blocking event loop
        logger.info(f"🧠 Running inference for {job_id}...")

        loop = asyncio.get_event_loop()
        result_data, temp_output_file = await loop.run_in_executor(
            None,  # Use default executor (ThreadPoolExecutor)
            run_inference_blocking,
            file_url,
            job_id
        )

        update_job_status(job_id, "PROCESSING", 80)
        logger.info(f"✅ Inference complete for {job_id}")

        # 3. Upload result to Firebase Storage
        result_url = upload_result_to_storage(
            temp_output_file,
            f"results/{user_id}/{job_id}/output.nii.gz"
        )

        logger.info(f"📤 Result uploaded for {job_id}: {result_url}")

        # 4. Final update to Firestore - COMPLETED
        update_job_status(
            job_id,
            "COMPLETED",
            100,
            result_url=result_url,
            metadata=result_data
        )
        logger.info(f"✅ Job {job_id} completed successfully")

    except Exception as e:
        logger.error(f"❌ Job {job_id} failed: {str(e)}", exc_info=True)
        try:
            update_job_status(job_id, "FAILED", 0, error_message=str(e))
        except Exception as update_e:
            logger.error(f"❌ Failed to update job status for {job_id}: {update_e}")

    finally:
        # Cleanup temp output file
        if temp_output_file and os.path.exists(temp_output_file):
            try:
                os.remove(temp_output_file)
                logger.info(f"🧹 Cleaned temp file: {temp_output_file}")
            except Exception as e:
                logger.warn(f"⚠️ Failed to clean temp file: {e}")

def run_inference_blocking(file_url: str, job_id: str):
    """
    Wrapper for blocking AI inference to run in executor.
    Needed because run_inference is async but CPU work must be in executor.
    """
    import asyncio
    loop = asyncio.new_event_loop()
    try:
        result = loop.run_until_complete(run_inference(file_url, job_id))
        return result
    finally:
        loop.close()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8000,
        log_level="info"
    )

