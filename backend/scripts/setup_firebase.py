"""Setup Firebase Firestore indexes and initial documents."""

from __future__ import annotations

import logging
import os
import sys

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def setup_firebase() -> None:
    """Initialise Firebase and create required Firestore index hints."""
    # Set GOOGLE_APPLICATION_CREDENTIALS before calling this script
    try:
        from app.core.firebase_admin import initialize_firebase, get_firestore_client
        initialize_firebase()
        db = get_firestore_client()

        # Verify connectivity
        test_ref = db.collection("_setup_check").document("ping")
        test_ref.set({"ok": True})
        test_ref.delete()
        logger.info("Firebase connectivity verified.")

        logger.info(
            "\nFirestore composite index recommendations:\n"
            "  Collection: jobs\n"
            "  Fields: user_id ASC, created_at DESC\n"
            "  Fields: status ASC, created_at ASC\n"
            "\nCreate these via: Firebase Console → Firestore → Indexes"
        )
    except Exception as exc:
        logger.error("Firebase setup failed: %s", exc)
        sys.exit(1)


if __name__ == "__main__":
    setup_firebase()
