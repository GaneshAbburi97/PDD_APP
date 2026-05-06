import firebase_admin
from firebase_admin import credentials, auth, firestore, storage
import os
import logging

logger = logging.getLogger("FIREBASE_UTILS")

# Initialize Firebase Admin SDK
# For research mode, ensure GOOGLE_APPLICATION_CREDENTIALS points to your service account key
if not firebase_admin._apps:
    cred = credentials.Certificate(os.getenv("GOOGLE_APPLICATION_CREDENTIALS", "service-account-key.json"))
    firebase_admin.initialize_app(cred, {
        'storageBucket': os.getenv("FIREBASE_STORAGE_BUCKET", "medical-processor-prod.appspot.com")
    })

db = firestore.client()
bucket = storage.bucket()

async def verify_firebase_token(authorization: str):
    if not authorization or not authorization.startswith("Bearer "):
        raise Exception("Missing or invalid authorization header")

    token = authorization.split("Bearer ")[1]
    try:
        decoded_token = auth.verify_id_token(token)
        return decoded_token
    except Exception as e:
        logger.error(f"❌ Token verification failed: {str(e)}")
        raise Exception("Unauthorized")

def update_job_status(job_id, status, progress, user_id=None, file_name=None, result_url=None, metadata=None, error_message=None):
    job_ref = db.collection("jobs").document(job_id)

    updates = {
        "status": status,
        "progress": progress,
        "updatedAt": int(os.times().elapsed * 1000) # Mock timestamp for research
    }

    if user_id: updates["userId"] = user_id
    if file_name: updates["fileName"] = file_name
    if result_url: updates["outputFileUrl"] = result_url
    if metadata: updates["metadata"] = metadata
    if error_message: updates["errorMessage"] = error_message

    job_ref.set(updates, merge=True)

def upload_result_to_storage(local_path, destination_path):
    blob = bucket.blob(destination_path)
    blob.upload_from_filename(local_path)
    blob.make_public()
    return blob.public_url
