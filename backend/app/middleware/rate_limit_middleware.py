"""Simple in-memory rate limiting middleware."""

from __future__ import annotations

import logging
import time
from collections import defaultdict, deque
from typing import Callable, Deque, Dict

from fastapi.responses import JSONResponse
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response
from starlette.types import ASGIApp

from app.core.config import settings

logger = logging.getLogger(__name__)

# uid -> deque of request timestamps
_request_windows: Dict[str, Deque[float]] = defaultdict(lambda: deque())


class RateLimitMiddleware(BaseHTTPMiddleware):
    """Limit requests to RATE_LIMIT_PER_MINUTE per authenticated user."""

    def __init__(self, app: ASGIApp) -> None:
        super().__init__(app)
        self._limit = settings.RATE_LIMIT_PER_MINUTE
        self._window = 60.0  # seconds

    async def dispatch(self, request: Request, call_next: Callable) -> Response:
        uid = getattr(request.state, "uid", None) or request.client.host if request.client else "anon"

        now = time.monotonic()
        window = _request_windows[uid]

        # Remove timestamps outside the current window
        while window and now - window[0] > self._window:
            window.popleft()

        if len(window) >= self._limit:
            return JSONResponse(
                status_code=429,
                content={"detail": "Rate limit exceeded. Please try again later."},
            )

        window.append(now)
        return await call_next(request)
