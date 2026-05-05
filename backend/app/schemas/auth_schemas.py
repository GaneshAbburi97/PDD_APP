"""Pydantic schemas for authentication."""

from __future__ import annotations

from pydantic import BaseModel, Field


class TokenVerifyRequest(BaseModel):
    id_token: str = Field(..., description="Firebase ID token")


class TokenVerifyResponse(BaseModel):
    uid: str
    email: str | None = None
    display_name: str | None = None
    email_verified: bool = False
