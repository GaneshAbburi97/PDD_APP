"""Firebase JWT authentication middleware."""

from __future__ import annotations

import logging
from typing import Callable, Dict

from fastapi import Request, Response
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.types import ASGIApp

from app.core.exceptions import AuthenticationError, InvalidTokenError, TokenExpiredError
from app.core.security import extract_bearer_token, verify_firebase_token

logger = logging.getLogger(__name__)

# Paths that do NOT require authentication
PUBLIC_PATHS = {"/health", "/docs", "/openapi.json", "/redoc", "/metrics"}


class AuthMiddleware(BaseHTTPMiddleware):
    """Verify Firebase JWT on every protected request and inject claims into request.state."""

    def __init__(self, app: ASGIApp) -> None:
        super().__init__(app)

    async def dispatch(self, request: Request, call_next: Callable) -> Response:
        # Skip authentication for public routes
        if request.url.path in PUBLIC_PATHS or request.url.path.startswith("/static"):
            return await call_next(request)

        auth_header = request.headers.get("Authorization", "")
        try:
            token = extract_bearer_token(auth_header)
            claims = verify_firebase_token(token)
            request.state.user = claims
            request.state.uid = claims.get("uid", "")
        except (InvalidTokenError, TokenExpiredError, AuthenticationError) as exc:
            from fastapi.responses import JSONResponse
            return JSONResponse(
                status_code=exc.status_code,
                content={"detail": exc.detail},
            )

        return await call_next(request)
