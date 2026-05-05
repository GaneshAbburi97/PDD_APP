"""Model version Firestore repository."""

from __future__ import annotations

import logging
from datetime import datetime
from typing import Any, Dict, List, Optional

from app.core.constants import COLLECTION_MODEL_VERSIONS
from app.repositories.firestore_repository import FirestoreRepository

logger = logging.getLogger(__name__)


class ModelRepository(FirestoreRepository):
    def __init__(self) -> None:
        super().__init__(COLLECTION_MODEL_VERSIONS)

    async def register_version(
        self,
        version_id: str,
        architecture: str,
        checkpoint_path: str,
        metrics: Optional[Dict[str, float]] = None,
        is_active: bool = False,
    ) -> None:
        doc = {
            "version_id": version_id,
            "architecture": architecture,
            "checkpoint_path": checkpoint_path,
            "metrics": metrics or {},
            "is_active": is_active,
            "created_at": datetime.utcnow().isoformat(),
        }
        await self.create(version_id, doc)

    async def get_active_version(self, architecture: str) -> Optional[Dict[str, Any]]:
        results = await self.list(
            filters=[("architecture", "==", architecture), ("is_active", "==", True)],
            limit=1,
        )
        return results[0] if results else None

    async def list_versions(self, architecture: Optional[str] = None) -> List[Dict[str, Any]]:
        filters = [("architecture", "==", architecture)] if architecture else None
        return await self.list(filters=filters, order_by="created_at")
