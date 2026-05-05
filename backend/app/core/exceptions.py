"""Custom exception hierarchy for the application."""

from typing import Any, Dict, Optional


class AppBaseException(Exception):
    """Base class for all application exceptions."""

    status_code: int = 500
    detail: str = "An unexpected error occurred."

    def __init__(
        self,
        detail: Optional[str] = None,
        context: Optional[Dict[str, Any]] = None,
    ) -> None:
        self.detail = detail or self.__class__.detail
        self.context = context or {}
        super().__init__(self.detail)


# ──────────────────────────────────────────────
# Authentication / authorisation
# ──────────────────────────────────────────────

class AuthenticationError(AppBaseException):
    status_code = 401
    detail = "Authentication failed."


class AuthorizationError(AppBaseException):
    status_code = 403
    detail = "You do not have permission to perform this action."


class TokenExpiredError(AuthenticationError):
    detail = "The provided token has expired."


class InvalidTokenError(AuthenticationError):
    detail = "The provided token is invalid."


# ──────────────────────────────────────────────
# Job / processing errors
# ──────────────────────────────────────────────

class JobNotFoundError(AppBaseException):
    status_code = 404
    detail = "Job not found."


class JobAlreadyExistsError(AppBaseException):
    status_code = 409
    detail = "A job with this ID already exists."


class JobProcessingError(AppBaseException):
    status_code = 500
    detail = "An error occurred during job processing."


class JobCancelledError(AppBaseException):
    status_code = 409
    detail = "The job has been cancelled."


class JobTimeoutError(AppBaseException):
    status_code = 504
    detail = "Job processing timed out."


# ──────────────────────────────────────────────
# File / storage errors
# ──────────────────────────────────────────────

class FileValidationError(AppBaseException):
    status_code = 422
    detail = "File validation failed."


class FileTooLargeError(FileValidationError):
    detail = "The uploaded file exceeds the maximum allowed size."


class UnsupportedFileTypeError(FileValidationError):
    detail = "The uploaded file type is not supported."


class StorageError(AppBaseException):
    status_code = 502
    detail = "A storage operation failed."


class FileDownloadError(StorageError):
    detail = "Failed to download the file from storage."


class FileUploadError(StorageError):
    detail = "Failed to upload the file to storage."


# ──────────────────────────────────────────────
# AI / inference errors
# ──────────────────────────────────────────────

class InferenceError(AppBaseException):
    status_code = 500
    detail = "AI inference failed."


class ModelNotFoundError(AppBaseException):
    status_code = 503
    detail = "The requested AI model checkpoint was not found."


class ModelLoadError(AppBaseException):
    status_code = 503
    detail = "Failed to load the AI model."


# ──────────────────────────────────────────────
# Firebase errors
# ──────────────────────────────────────────────

class FirebaseError(AppBaseException):
    status_code = 502
    detail = "A Firebase operation failed."


class FirestoreError(FirebaseError):
    detail = "A Firestore operation failed."


class FirebaseStorageError(FirebaseError):
    detail = "A Firebase Storage operation failed."


# ──────────────────────────────────────────────
# Rate limiting
# ──────────────────────────────────────────────

class RateLimitExceededError(AppBaseException):
    status_code = 429
    detail = "Rate limit exceeded. Please try again later."


# ──────────────────────────────────────────────
# Validation
# ──────────────────────────────────────────────

class ValidationError(AppBaseException):
    status_code = 422
    detail = "Request validation failed."
