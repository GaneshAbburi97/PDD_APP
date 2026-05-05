"""backend/app/api/v1/auth.py — Firebase token verification endpoint."""

from __future__ import annotations

import logging

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from firebase_admin import auth as firebase_auth
from pydantic import BaseModel

from app.core.firebase_admin import init_firebase

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/auth", tags=["auth"])
_bearer = HTTPBearer()


class TokenVerifyRequest(BaseModel):
    id_token: str


class TokenVerifyResponse(BaseModel):
    uid: str
    email: str | None
    email_verified: bool


# ── Dependency ──────────────────────────────────────────────────────────────

async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(_bearer),
) -> dict:
    """
    FastAPI dependency that verifies the Firebase Bearer token on every
    protected endpoint.

    Raises HTTP 401 if the token is missing, expired, or invalid.
    """
    init_firebase()
    token = credentials.credentials
    try:
        decoded = firebase_auth.verify_id_token(token)
        return decoded
    except firebase_auth.ExpiredIdTokenError:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Token expired")
    except firebase_auth.InvalidIdTokenError as exc:
        logger.warning("Invalid Firebase token: %s", exc)
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")
    except Exception as exc:
        logger.error("Token verification error: %s", exc)
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Could not verify token")


# ── Endpoints ───────────────────────────────────────────────────────────────

@router.post("/verify", response_model=TokenVerifyResponse)
async def verify_token(body: TokenVerifyRequest):
    """
    Verify a Firebase ID token and return the decoded claims.
    Useful for the Android app to confirm the token is still valid.
    """
    init_firebase()
    try:
        decoded = firebase_auth.verify_id_token(body.id_token)
        return TokenVerifyResponse(
            uid=decoded["uid"],
            email=decoded.get("email"),
            email_verified=decoded.get("email_verified", False),
        )
    except Exception as exc:
        logger.warning("Token verify failed: %s", exc)
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid or expired token")
