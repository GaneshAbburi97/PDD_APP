"""Security utilities: Firebase JWT verification."""

from __future__ import annotations

import logging
from typing import Dict

import firebase_admin.auth as fb_auth

from app.core.exceptions import AuthenticationError, InvalidTokenError, TokenExpiredError
from app.core.firebase_admin import initialize_firebase

logger = logging.getLogger(__name__)


def verify_firebase_token(id_token: str) -> Dict:
    """Verify a Firebase ID token and return the decoded claims.

    Args:
        id_token: Firebase ID token from the Android client.

    Returns:
        Decoded token claims dict (uid, email, etc.).

    Raises:
        TokenExpiredError: If the token has expired.
        InvalidTokenError: If the token is malformed or invalid.
        AuthenticationError: For any other Firebase auth error.
    """
    initialize_firebase()

    try:
        decoded = fb_auth.verify_id_token(id_token, check_revoked=True)
        logger.debug("Token verified for uid=%s", decoded.get("uid"))
        return decoded
    except fb_auth.ExpiredIdTokenError as exc:
        raise TokenExpiredError() from exc
    except fb_auth.RevokedIdTokenError as exc:
        raise TokenExpiredError(detail="Token has been revoked.") from exc
    except (
        fb_auth.InvalidIdTokenError,
        fb_auth.CertificateFetchError,
        ValueError,
    ) as exc:
        raise InvalidTokenError() from exc
    except Exception as exc:
        logger.error("Unexpected Firebase auth error: %s", exc)
        raise AuthenticationError() from exc


def extract_bearer_token(authorization_header: str) -> str:
    """Extract the token from an 'Authorization: Bearer <token>' header.

    Raises:
        InvalidTokenError: If the header is missing or malformed.
    """
    if not authorization_header:
        raise InvalidTokenError(detail="Authorization header is missing.")

    parts = authorization_header.strip().split()
    if len(parts) != 2 or parts[0].lower() != "bearer":
        raise InvalidTokenError(detail="Authorization header must be 'Bearer <token>'.")

    return parts[1]
