import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    PROJECT_ID: str = os.getenv("PROJECT_ID", "medical-processor-prod")
    STORAGE_BUCKET: str = os.getenv("STORAGE_BUCKET", "medical-processor-prod.appspot.com")

    class Config:
        env_file = ".env"

settings = Settings()
