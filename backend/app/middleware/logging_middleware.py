"""Structured request/response logging middleware."""

from __future__ import annotations

import logging
import time
import uuid
from typing import Callable

from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response
from starlette.types import ASGIApp

logger = logging.getLogger("access")


class LoggingMiddleware(BaseHTTPMiddleware):
    def __init__(self, app: ASGIApp) -> None:
        super().__init__(app)

    async def dispatch(self, request: Request, call_next: Callable) -> Response:
        request_id = str(uuid.uuid4())[:8]
        t0 = time.perf_counter()

        response = await call_next(request)

        elapsed_ms = (time.perf_counter() - t0) * 1000
        uid = getattr(request.state, "uid", "-")

        logger.info(
            "request completed",
            extra={
                "request_id": request_id,
                "method": request.method,
                "path": request.url.path,
                "status_code": response.status_code,
                "elapsed_ms": round(elapsed_ms, 2),
                "uid": uid,
            },
        )
        response.headers["X-Request-ID"] = request_id
        return response
