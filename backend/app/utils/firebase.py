import firebase_admin
from firebase_admin import credentials, auth, firestore, storage
import os
import logging
import time
import json
from datetime import timedelta
from typing import Optional, Dict, Any
from fastapi import Header, HTTPException

logger = logging.getLogger("FIREBASE_UTILS")

# ===== Singleton Firebase Admin Initialization =====
_initialized = False
db = None
bucket = None

# Local persistent database path for running without active Firebase credentials
_local_db_path = os.path.join("data", "local_jobs_db.json")

def _is_auth_bypass_enabled() -> bool:
    return os.getenv("ALLOW_AUTH_BYPASS", "").lower() in {"1", "true", "yes"}

def _load_local_db() -> Dict[str, Any]:
    if os.path.exists(_local_db_path):
        try:
            with open(_local_db_path, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception as e:
            logger.error(f"❌ Error loading local database JSON: {e}")
    return {}

def _save_local_db(db_data: Dict[str, Any]):
    try:
        os.makedirs(os.path.dirname(_local_db_path), exist_ok=True)
        with open(_local_db_path, "w", encoding="utf-8") as f:
            json.dump(db_data, f, indent=2)
    except Exception as e:
        logger.error(f"❌ Error saving local database JSON: {e}")

def initialize_firebase():
    """
    Initialize Firebase Admin SDK only once.
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
        logger.info("✅ Firebase Admin SDK initialized successfully")
        return True

    except ValueError as e:
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

async def verify_firebase_token(authorization: Optional[str] = Header(None)) -> Dict[str, Any]:
    """
    Verify Firebase ID token from Authorization header.
    Supports a mock token bypass only when ALLOW_AUTH_BYPASS is explicitly enabled.
    """
    auth_bypass_enabled = _is_auth_bypass_enabled()

    if not authorization or not authorization.startswith("Bearer "):
        if auth_bypass_enabled:
            logger.warning("🧪 [MOCK MODE] Missing auth header, using explicit bypass user")
            return {
                "uid": "mock_testuser",
                "email": "testuser@medical.com",
                "name": "Mock Test User"
            }
        raise HTTPException(status_code=401, detail="Missing or invalid authorization header")

    token = authorization.split("Bearer ")[1]

    try:
        if not _initialized:
            initialize_firebase()

        if (token.startswith("mock-") or token == "test-token") and auth_bypass_enabled:
            logger.warning("🧪 [MOCK MODE] Using explicit bypass token")
            username = token.replace("mock-", "")
            return {
                "uid": f"user_{username}",
                "email": f"{username}@medical.com",
                "name": f"Mock User {username}"
            }

        if not _initialized:
            raise HTTPException(status_code=503, detail="Authentication service unavailable")

        decoded_token = auth.verify_id_token(token)
        return decoded_token

    except auth.RevokedIdTokenError:
        logger.warning(f"❌ Token has been revoked: {token[:15]}...")
        raise HTTPException(status_code=401, detail="Token has been revoked")
    except auth.ExpiredIdTokenError:
        logger.warning(f"❌ Token has expired: {token[:15]}...")
        raise HTTPException(status_code=401, detail="Token has expired")
    except Exception as e:
        logger.error(f"❌ Token verification failed: {str(e)}")
        if auth_bypass_enabled:
            logger.warning("🧪 [MOCK MODE] Verification failed, using explicit bypass fallback user")
            return {
                "uid": "mock_fallback_user",
                "email": "fallback@medical.com",
                "name": "Fallback User"
            }
        raise HTTPException(status_code=401, detail=f"Unauthorized: {str(e)}")

def update_job_status(
    job_id: str,
    status: str,
    progress: int,
    user_id: Optional[str] = None,
    user_email: Optional[str] = None,
    file_name: Optional[str] = None,
    input_file_url: Optional[str] = None,
    file_size: Optional[int] = None,
    created_at: Optional[int] = None,
    result_url: Optional[str] = None,
    metadata: Optional[Dict] = None,
    error_message: Optional[str] = None
):
    """
    Update job status in Firestore or local persistent JSON database.
    """
    try:
        if not _initialized:
            initialize_firebase()

        updates = {
            "jobId": job_id,
            "status": status,
            "progress": progress,
            "updatedAt": int(time.time() * 1000)
        }

        if status == "QUEUED" and (not user_id or not file_name or not input_file_url):
            raise ValueError("QUEUED status requires user_id, file_name, and input_file_url during job creation")

        if user_id:
            updates["userId"] = user_id
        if user_email:
            updates["userEmail"] = user_email
        if file_name:
            updates["fileName"] = file_name
        if input_file_url:
            updates["inputFileUrl"] = input_file_url
        if file_size is not None:
            updates["fileSize"] = file_size
        if created_at is not None:
            updates["createdAt"] = created_at
        if result_url:
            updates["outputFileUrl"] = result_url
        if metadata:
            # Ensure metadata dictionary is string-string to comply with Android's Map<String, String> expectations
            updates["metadata"] = {str(k): str(v) for k, v in metadata.items()}
        if error_message:
            updates["errorMessage"] = error_message

        # Sync to Firestore if available
        if db is not None:
            job_ref = db.collection("jobs").document(job_id)
            job_ref.set(updates, merge=True)
            logger.debug(f"📝 Job status updated in Firestore: {job_id} -> {status} ({progress}%)")
        else:
            # Sync to local persistent JSON database
            local_db = _load_local_db()
            if job_id not in local_db:
                local_db[job_id] = {}
            local_db[job_id].update(updates)
            _save_local_db(local_db)
            logger.info(f"📝 [PERSISTENT LOCAL DB] Job status updated: {job_id} -> {status} ({progress}%)")

    except Exception as e:
        logger.error(f"❌ Failed to update status for {job_id}: {e}", exc_info=True)

def get_job_data(job_id: str) -> Optional[Dict[str, Any]]:
    """
    Retrieve job record from Firestore or local persistent JSON database.
    """
    try:
        if not _initialized:
            initialize_firebase()

        if db is not None:
            job_ref = db.collection("jobs").document(job_id)
            doc = job_ref.get()
            if doc.exists:
                return doc.to_dict()
            return None
        else:
            local_db = _load_local_db()
            return local_db.get(job_id)
    except Exception as e:
        logger.error(f"❌ Error retrieving job data for {job_id}: {e}")
        local_db = _load_local_db()
        return local_db.get(job_id)

def upload_result_to_storage(local_path: str, destination_path: str) -> Optional[str]:
    """
    Upload processing output file to Firebase Storage.
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
        signed_url = blob.generate_signed_url(version="v4", expiration=timedelta(hours=1), method="GET")

        logger.info(f"✅ File uploaded to storage: {destination_path}")
        return signed_url

    except Exception as e:
        logger.error(f"❌ Failed to upload result: {e}", exc_info=True)
        return None

# Initialize on import
initialize_firebase()
firebase_initialized = _initialized
