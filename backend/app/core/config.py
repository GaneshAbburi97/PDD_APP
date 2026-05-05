"""backend/app/core/config.py — Application configuration via Pydantic Settings."""

from __future__ import annotations

from functools import lru_cache
from pathlib import Path
from typing import List

from pydantic import field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )

    # ── Application ─────────────────────────────────────────────────────────
    app_name: str = "Medical AI Backend"
    app_version: str = "1.0.0"
    debug: bool = False

    # ── Server ───────────────────────────────────────────────────────────────
    host: str = "0.0.0.0"
    port: int = 8000

    # ── CORS ─────────────────────────────────────────────────────────────────
    allowed_origins: List[str] = [
        "http://localhost",
        "http://10.0.2.2",
        "http://127.0.0.1",
    ]

    # ── Firebase ─────────────────────────────────────────────────────────────
    firebase_credentials_path: str = "firebase-credentials.json"
    firebase_storage_bucket: str = ""

    # ── AI Model ─────────────────────────────────────────────────────────────
    model_checkpoint_path: str = "models/ian_unet.pth"
    model_input_size: int = 512
    inference_device: str = "cpu"  # "cpu" or "cuda"
    inference_timeout_seconds: int = 120

    # ── Storage ──────────────────────────────────────────────────────────────
    temp_dir: str = "/tmp/medical_processor"

    @field_validator("temp_dir")
    @classmethod
    def create_temp_dir(cls, v: str) -> str:
        Path(v).mkdir(parents=True, exist_ok=True)
        return v


@lru_cache
def get_settings() -> Settings:
    return Settings()
