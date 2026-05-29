import asyncio
import logging
import os
import shutil
import tempfile
import uuid
from contextlib import asynccontextmanager

from fastapi import FastAPI, HTTPException, Depends, BackgroundTasks, File, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel

from .utils.firebase import verify_firebase_token, update_job_status, upload_result_to_storage
from .ai.segmentation import run_inference, cleanup_model

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='[%(asctime)s] %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger("RESEARCH_BACKEND")

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

    logger.info("🚀 Starting Medical Processor Backend (Research Mode)")
    try:
        from .utils.firebase import db, bucket
        firebase_initialized = True
        logger.info("✅ Firebase initialized")
    except Exception as e:
        logger.error(f"❌ Firebase init failed: {e}")
        firebase_initialized = False

    temp_dir = os.path.join(tempfile.gettempdir(), "medical_processor")
    os.makedirs(temp_dir, exist_ok=True)
    logger.info(f"📁 Temp directory: {temp_dir}")

    yield

    logger.info("🛑 Shutting down Medical Processor Backend")

    try:
        if os.path.exists(temp_dir):
            shutil.rmtree(temp_dir, ignore_errors=True)
            logger.info("🧹 Cleaned temp directory")
    except Exception as e:
        logger.error(f"⚠️ Error cleaning temp directory: {e}")

    try:
        cleanup_model()
        logger.info("🧹 AI models unloaded")
    except Exception as e:
        logger.error(f"⚠️ Error cleaning AI models: {e}")

    logger.info("✅ Shutdown complete")

app = FastAPI(
    title="Medical File Processor API",
    description="FastAPI backend for medical file processing with Firebase integration",
    version="1.0.0-research",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

temp_dir_path = os.path.join(tempfile.gettempdir(), "medical_processor")
os.makedirs(temp_dir_path, exist_ok=True)
app.mount("/static", StaticFiles(directory=temp_dir_path), name="static")

class ProcessRequest(BaseModel):
    fileUrl: str
    fileName: str
    fileSize: int
    cloudProvider: str
    userEmail: str

class RegisterRequest(BaseModel):
    email: str
    password: str
    displayName: str

class LoginRequest(BaseModel):
    email: str
    password: str

class ApiResponse(BaseModel):
    success: bool
    message: str
    data: dict | None = None

@app.get("/health")
async def check_health():
    return {
        "success": True,
        "message": "Backend is online (Research Mode)",
        "data": {
            "status": "healthy",
            "version": "1.0.0-research",
            "firebase": "initialized" if firebase_initialized else "not initialized"
        }
    }

@app.post("/auth/register")
async def register(request: RegisterRequest):
    try:
        from firebase_admin import auth
        user_id = f"mock_{uuid.uuid4().hex[:8]}"
        display_name = request.displayName

        if firebase_initialized:
            try:
                user_record = auth.create_user(
                    email=request.email,
                    password=request.password,
                    display_name=request.displayName
                )
                user_id = user_record.uid
            except Exception as fe:
                logger.warning(f"Firebase auth create failed: {fe}. Using local user ID: {user_id}")

        return {
            "success": True,
            "message": "Registration successful",
            "data": {
                "uid": user_id,
                "email": request.email,
                "displayName": display_name,
                "photoUrl": None
            }
        }
    except Exception as e:
        logger.error(f"❌ Registration failed: {e}")
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/auth/login")
async def login(request: LoginRequest):
    try:
        from firebase_admin import auth
        user_id = f"mock_{uuid.uuid4().hex[:8]}"
        display_name = "Mock User"

        if firebase_initialized:
            try:
                user_record = auth.get_user_by_email(request.email)
                user_id = user_record.uid
                display_name = user_record.display_name or "Medical User"
            except Exception as fe:
                logger.warning(f"Firebase auth get user failed: {fe}. Using local user ID: {user_id}")

        return {
            "success": True,
            "message": "Login successful",
            "data": {
                "uid": user_id,
                "email": request.email,
                "displayName": display_name,
                "photoUrl": None
            }
        }
    except Exception as e:
        logger.error(f"❌ Login failed: {e}")
        raise HTTPException(status_code=400, detail=str(e))

@app.post("/upload")
async def upload_file(file: UploadFile = File(...), user=Depends(verify_firebase_token)):
    temp_dir = os.path.join(tempfile.gettempdir(), "medical_processor")
    os.makedirs(temp_dir, exist_ok=True)

    temp_path = os.path.join(temp_dir, f"{uuid.uuid4()}_{file.filename}")
    try:
        with open(temp_path, "wb") as f:
            shutil.copyfileobj(file.file, f)

        user_id = user.get("uid", "unknown") if isinstance(user, dict) else user.uid
        destination_path = f"uploads/{user_id}/{file.filename}"
        
        file_url = None
        if firebase_initialized:
            try:
                file_url = upload_result_to_storage(temp_path, destination_path)
            except Exception as se:
                logger.error(f"❌ Storage upload failed: {se}")

        if not file_url:
            # Fallback to local URL for research mode
            file_url = f"http://10.0.2.2:8000/static/{file.filename}"
            logger.info(f"Using fallback local URL: {file_url}")

        return {
            "success": True,
            "message": "File uploaded successfully",
            "data": {
                "fileUrl": file_url,
                "fileName": file.filename
            }
        }
    finally:
        if os.path.exists(temp_path):
            os.remove(temp_path)

@app.post("/process")
@app.post("/process/start")
async def start_processing(
    request: ProcessRequest,
    background_tasks: BackgroundTasks,
    user=Depends(verify_firebase_token)
):
    if not firebase_initialized:
        raise HTTPException(status_code=503, detail="Firebase not initialized")

    job_id = str(uuid.uuid4())
    user_id = user.get("uid", "unknown") if isinstance(user, dict) else user.uid

    logger.info(f"🚀 Starting processing for job: {job_id} (user: {user_id})")

    try:
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

@app.get("/process/status/{jobId}")
async def get_job_status(jobId: str, user=Depends(verify_firebase_token)):
    if not firebase_initialized:
        raise HTTPException(status_code=503, detail="Firebase not initialized")

    try:
        from .utils.firebase import db
        job_ref = db.collection("jobs").document(jobId)
        doc = job_ref.get()
        if not doc.exists:
            raise HTTPException(status_code=404, detail="Job not found")

        job_data = doc.to_dict()

        return {
            "success": True,
            "message": "Job status retrieved",
            "data": {
                "jobId": job_data.get("jobId", jobId),
                "fileName": job_data.get("fileName", "unknown"),
                "status": job_data.get("status", "QUEUED"),
                "progress": job_data.get("progress", 0),
                "createdAt": job_data.get("createdAt", 0),
                "updatedAt": job_data.get("updatedAt", 0),
                "resultUrl": job_data.get("outputFileUrl"),
                "metadata": job_data.get("metadata"),
                "errorMessage": job_data.get("errorMessage")
            }
        }
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error getting job status: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/process/result/{jobId}")
async def get_processing_result(jobId: str, user=Depends(verify_firebase_token)):
    if not firebase_initialized:
        raise HTTPException(status_code=503, detail="Firebase not initialized")

    try:
        from .utils.firebase import db
        job_ref = db.collection("jobs").document(jobId)
        doc = job_ref.get()
        if not doc.exists:
            raise HTTPException(status_code=404, detail="Job not found")

        job_data = doc.to_dict()
        status = job_data.get("status", "QUEUED")

        if status != "COMPLETED":
            raise HTTPException(status_code=400, detail=f"Job is not completed. Current status: {status}")

        return {
            "success": True,
            "message": "Job result retrieved",
            "data": {
                "jobId": job_data.get("jobId", jobId),
                "status": status,
                "resultUrl": job_data.get("outputFileUrl", ""),
                "metadata": job_data.get("metadata", {}),
                "processingTimeSeconds": 120,
                "fileSize": job_data.get("fileSize", 0)
            }
        }
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error getting job result: {e}")
        raise HTTPException(status_code=500, detail=str(e))

async def process_job(job_id: str, file_url: str, file_name: str, user_id: str):
    temp_output_file = None

    try:
        update_job_status(job_id, "PROCESSING", 10)
        logger.info(f"📊 Job {job_id} moved to PROCESSING")

        logger.info(f"🧠 Running inference for {job_id}...")

        loop = asyncio.get_event_loop()
        result_data, temp_output_file = await loop.run_in_executor(
            None,
            run_inference_blocking,
            file_url,
            job_id
        )

        update_job_status(job_id, "PROCESSING", 80)
        logger.info(f"✅ Inference complete for {job_id}")

        result_url = upload_result_to_storage(
            temp_output_file,
            f"results/{user_id}/{job_id}/output.nii.gz"
        )
        if not result_url:
            result_url = f"http://10.0.2.2:8000/static/{job_id}_result.nii.gz"

        logger.info(f"📤 Result uploaded for {job_id}: {result_url}")

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
        if temp_output_file and os.path.exists(temp_output_file):
            try:
                os.remove(temp_output_file)
                logger.info(f"🧹 Cleaned temp file: {temp_output_file}")
            except Exception as e:
                logger.warning(f"⚠️ Failed to clean temp file: {e}")

def run_inference_blocking(file_url: str, job_id: str):
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