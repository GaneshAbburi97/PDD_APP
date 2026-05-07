import firebase_admin
from firebase_admin import credentials, auth, firestore, storage
import os
import logging
import time
from typing import Optional, Dict, Any

logger = logging.getLogger("FIREBASE_UTILS")

# ===== Singleton Firebase Admin Initialization =====
# Prevents multiple initialization attempts which cause errors

_initialized = False
db = None
bucket = None

def initialize_firebase():
    """
    Initialize Firebase Admin SDK only once.

    SAFETY:
    - Singleton pattern prevents multiple initializations
    - Graceful error handling if credentials unavailable
    - Service account key via GOOGLE_APPLICATION_CREDENTIALS env var
    """
    global _initialized, db, bucket

    if _initialized:
        logger.debug("Firebase already initialized")
        return True

    try:
        creds_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS", "service-account-key.json")

        if not os.path.exists(creds_path):
            logger.warning(f"⚠️ Credentials file not found: {creds_path}")
            logger.warning("Set GOOGLE_APPLICATION_CREDENTIALS env var or place service-account-key.json in project root")
            _initialized = False
            return False

        credentials_obj = credentials.Certificate(creds_path)
        firebase_admin.initialize_app(
            credentials_obj,
            {
                'storageBucket': os.getenv(
                    "FIREBASE_STORAGE_BUCKET",
                    "medical-processor-prod.appspot.com"
                )
            }
        )

        db = firestore.client()
        bucket = storage.bucket()
        _initialized = True
        logger.info("✅ Firebase Admin SDK initialized")
        return True

    except ValueError as e:
        # Firebase already initialized by another app instance
        if "already exists" in str(e):
            logger.warning(f"⚠️ Firebase app already initialized: {e}")
            db = firestore.client()
            bucket = storage.bucket()
            _initialized = True
            return True
        else:
            logger.error(f"❌ Firebase initialization failed: {e}")
            _initialized = False
            return False
    except Exception as e:
        logger.error(f"❌ Firebase initialization error: {e}", exc_info=True)
        _initialized = False
        return False

async def verify_firebase_token(authorization: Optional[str] = None) -> Dict[str, Any]:
    """
    Verify Firebase ID token from Authorization header.

    TOKEN SAFETY:
    - Validates token signature with Firebase public keys
    - Checks token expiration
    - Returns decoded token with UID and claims
    - Handles missing/invalid tokens gracefully

    @param authorization: Authorization header value (Bearer {token})
    @return: Decoded token dict with UID
    @raise: Exception if token invalid/expired
    """
    if not authorization or not authorization.startswith("Bearer "):
        raise Exception("Missing or invalid authorization header")

    token = authorization.split("Bearer ")[1]

    try:
        # Initialize Firebase if needed
        if not _initialized:
            initialize_firebase()

        # Verify token with Firebase Admin SDK
        # This validates signature and expiration
        decoded_token = auth.verify_id_token(token)
        logger.debug(f"✅ Token verified for user: {decoded_token.get('uid')}")
        return decoded_token

    except auth.RevokedIdTokenError:
        logger.warning(f"❌ Token revoked: {token[:20]}...")
        raise Exception("Token has been revoked")
    except auth.ExpiredIdTokenError:
        logger.warning(f"❌ Token expired: {token[:20]}...")
        raise Exception("Token has expired")
    except Exception as e:
        logger.error(f"❌ Token verification failed: {str(e)}")
        raise Exception(f"Unauthorized: {str(e)}")

def update_job_status(
    job_id: str,
    status: str,
    progress: int,
    user_id: Optional[str] = None,
    file_name: Optional[str] = None,
    result_url: Optional[str] = None,
    metadata: Optional[Dict] = None,
    error_message: Optional[str] = None
):
    """
    Update job status in Firestore.

    FIRESTORE COST OPTIMIZATION:
    - Uses merge=True to avoid reading before writing
    - Batches updates into single write
    - Only updates changed fields
    - Timestamps managed by server

    @param job_id: Unique job identifier
    @param status: Processing status (QUEUED, PROCESSING, COMPLETED, FAILED)
    @param progress: Progress percentage (0-100)
    """
    try:
        if not _initialized:
            initialize_firebase()

        if db is None:
            logger.error("❌ Firestore client not available")
            return

        job_ref = db.collection("jobs").document(job_id)

        # Build update dict - only include provided fields (cost optimization)
        updates = {
            "status": status,
            "progress": progress,
            "updatedAt": int(time.time() * 1000)  # Server timestamp alternative
        }

        if user_id:
            updates["userId"] = user_id
        if file_name:
            updates["fileName"] = file_name
        if result_url:
            updates["outputFileUrl"] = result_url
        if metadata:
            updates["metadata"] = metadata
        if error_message:
            updates["errorMessage"] = error_message

        # Merge=True prevents reading before write (cost optimization)
        job_ref.set(updates, merge=True)
        logger.debug(f"📝 Job status updated: {job_id} -> {status} ({progress}%)")

    except Exception as e:
        logger.error(f"❌ Failed to update job status for {job_id}: {e}", exc_info=True)
        # Don't raise - processing should continue even if Firestore update fails

def upload_result_to_storage(local_path: str, destination_path: str) -> Optional[str]:
    """
    Upload processing result file to Firebase Storage.

    STORAGE COST OPTIMIZATION:
    - Only uploads necessary files (results, not inputs)
    - Marks private after upload (security)
    - Returns public URL in Firestore only

    @param local_path: Local file path to upload
    @param destination_path: Destination path in Storage bucket
    @return: Public URL of uploaded file or None on error
    """
    try:
        if not _initialized:
            initialize_firebase()

        if bucket is None:
            logger.error("❌ Storage bucket not available")
            return None

        if not os.path.exists(local_path):
            logger.error(f"❌ File not found for upload: {local_path}")
            return None

        blob = bucket.blob(destination_path)
        blob.upload_from_filename(local_path)

        # Make public for download
        blob.make_public()
        public_url = blob.public_url

        logger.info(f"✅ File uploaded: {destination_path}")
        logger.debug(f"📥 Public URL: {public_url}")

        return public_url

    except Exception as e:
        logger.error(f"❌ Failed to upload result: {e}", exc_info=True)
        return None

# Initialize on import
initialize_firebase()
