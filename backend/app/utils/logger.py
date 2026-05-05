"""Structured logging configuration."""

from __future__ import annotations

import logging
import logging.config
import sys
from typing import Any, Dict

from app.core.config import settings


def _json_formatter_config() -> Dict[str, Any]:
    return {
        "()": "pythonjsonlogger.jsonlogger.JsonFormatter",
        "format": "%(asctime)s %(name)s %(levelname)s %(message)s",
    }


def _text_formatter_config() -> Dict[str, Any]:
    return {
        "format": "%(asctime)s [%(levelname)s] %(name)s: %(message)s",
        "datefmt": "%Y-%m-%dT%H:%M:%S",
    }


def configure_logging() -> None:
    """Apply logging configuration based on settings."""

    use_json = settings.LOG_FORMAT.lower() == "json"
    formatter_config = _json_formatter_config() if use_json else _text_formatter_config()

    config: Dict[str, Any] = {
        "version": 1,
        "disable_existing_loggers": False,
        "formatters": {
            "default": formatter_config,
        },
        "handlers": {
            "console": {
                "class": "logging.StreamHandler",
                "stream": sys.stdout,
                "formatter": "default",
            },
        },
        "root": {
            "handlers": ["console"],
            "level": settings.LOG_LEVEL,
        },
        "loggers": {
            "uvicorn": {"level": "INFO", "propagate": True},
            "uvicorn.access": {"level": "WARNING", "propagate": True},
            "firebase_admin": {"level": "WARNING", "propagate": True},
        },
    }

    logging.config.dictConfig(config)
