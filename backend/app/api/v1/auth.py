"""Authentication endpoints."""

from __future__ import annotations

from fastapi import APIRouter, Request

from app.schemas.auth_schemas import TokenVerifyResponse

router = APIRouter(prefix="/auth", tags=["auth"])


@router.get("/me", response_model=TokenVerifyResponse)
async def get_current_user(request: Request) -> TokenVerifyResponse:
    """Return the authenticated user's claims (verified by middleware)."""
    claims = request.state.user
    return TokenVerifyResponse(
        uid=claims.get("uid", ""),
        email=claims.get("email"),
        display_name=claims.get("name"),
        email_verified=claims.get("email_verified", False),
    )
