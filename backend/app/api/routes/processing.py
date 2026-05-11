from fastapi import APIRouter

router = APIRouter()

@router.post("/start")
async def start_processing():
    return {"message": "Processing started", "job_id": "mock_job_id"}
