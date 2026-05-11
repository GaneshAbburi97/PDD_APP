import firebase_admin
from firebase_admin import credentials, firestore, storage

def initialize_firebase():
    if not firebase_admin._apps:
        cred = credentials.ApplicationDefault()
        firebase_admin.initialize_app(cred)

db = firestore.client()
bucket = storage.bucket()
