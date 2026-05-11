from fastapi import APIRouter
from app.models.schemas import LoginRequest

router = APIRouter()

@router.post("/login")
async def login(request: LoginRequest):
    return {"message": "Login successful", "user_id": "mock_user_id"}
