"""Application configuration via Pydantic v2 settings."""

from __future__ import annotations

import os
from functools import lru_cache
from typing import List, Optional

from pydantic import Field, field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )

    # Application
    APP_NAME: str = "PDD Medical AI Platform"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = False
    ENVIRONMENT: str = "production"  # development | staging | production

    # Server
    HOST: str = "0.0.0.0"
    PORT: int = 8000
    WORKERS: int = 1
    RELOAD: bool = False

    # CORS
    CORS_ORIGINS: List[str] = Field(default=["*"])
    CORS_ALLOW_CREDENTIALS: bool = True

    # Firebase / Google Cloud
    FIREBASE_PROJECT_ID: str = "medical-processor-prod"
    FIREBASE_STORAGE_BUCKET: str = "medical-processor-prod.appspot.com"
    GOOGLE_APPLICATION_CREDENTIALS: Optional[str] = None  # path to service-account JSON
    FIREBASE_SERVICE_ACCOUNT_JSON: Optional[str] = None   # inline JSON string (base64 or raw)

    # JWT
    JWT_ALGORITHM: str = "RS256"

    # Rate limiting (requests per minute per user)
    RATE_LIMIT_PER_MINUTE: int = 60

    # Logging
    LOG_LEVEL: str = "INFO"
    LOG_FORMAT: str = "json"  # json | text

    # Temporary file storage
    TEMP_DIR: str = "/tmp/pdd_processing"

    # AI / Model paths
    MODEL_CHECKPOINT_DIR: str = "/app/checkpoints"
    DEFAULT_MODEL_ARCH: str = "unet"
    MODEL_DEVICE: str = "auto"  # auto | cpu | cuda

    # Worker settings
    WORKER_CONCURRENCY: int = 4
    WORKER_POLL_INTERVAL_SECONDS: float = 2.0

    # File validation
    MAX_FILE_SIZE_MB: int = 500

    @field_validator("CORS_ORIGINS", mode="before")
    @classmethod
    def parse_cors_origins(cls, v: object) -> List[str]:
        if isinstance(v, str):
            return [origin.strip() for origin in v.split(",")]
        return v  # type: ignore[return-value]

    @field_validator("LOG_LEVEL")
    @classmethod
    def validate_log_level(cls, v: str) -> str:
        allowed = {"DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL"}
        upper = v.upper()
        if upper not in allowed:
            raise ValueError(f"LOG_LEVEL must be one of {allowed}")
        return upper


@lru_cache()
def get_settings() -> Settings:
    return Settings()


settings = get_settings()
