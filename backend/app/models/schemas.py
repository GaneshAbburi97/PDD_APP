from pydantic import BaseModel

class LoginRequest(BaseModel):
    email: str
    password: str

class ProcessingJob(BaseModel):
    job_id: str
    status: str
