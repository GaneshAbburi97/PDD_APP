"""User Firestore repository."""

from __future__ import annotations

import logging
from datetime import datetime
from typing import Any, Dict, Optional

from app.core.constants import COLLECTION_USERS
from app.repositories.firestore_repository import FirestoreRepository

logger = logging.getLogger(__name__)


class UserRepository(FirestoreRepository):
    def __init__(self) -> None:
        super().__init__(COLLECTION_USERS)

    async def get_or_create_user(self, uid: str, email: Optional[str] = None, display_name: Optional[str] = None) -> Dict[str, Any]:
        existing = await self.get(uid)
        if existing:
            return existing
        user_doc = {
            "uid": uid,
            "email": email,
            "display_name": display_name,
            "roles": ["user"],
            "created_at": datetime.utcnow().isoformat(),
        }
        await self.create(uid, user_doc)
        logger.info("Created new user document uid=%s", uid)
        return user_doc

    async def get_user(self, uid: str) -> Optional[Dict[str, Any]]:
        return await self.get(uid)

    async def update_user_roles(self, uid: str, roles: list) -> None:
        await self.update(uid, {"roles": roles, "updated_at": datetime.utcnow().isoformat()})
