"""Firestore base repository with CRUD helpers."""

from __future__ import annotations

import logging
from typing import Any, Dict, List, Optional

from google.cloud.firestore_v1 import AsyncClient
from google.cloud.firestore_v1.base_query import BaseQuery

from app.core.firebase_admin import get_firestore_client

logger = logging.getLogger(__name__)


class FirestoreRepository:
    """Generic async Firestore repository."""

    def __init__(self, collection: str) -> None:
        self.collection = collection

    def _client(self):
        return get_firestore_client()

    def _col(self):
        return self._client().collection(self.collection)

    async def get(self, doc_id: str) -> Optional[Dict[str, Any]]:
        doc_ref = self._col().document(doc_id)
        doc = doc_ref.get()
        if doc.exists:
            data = doc.to_dict()
            data["id"] = doc.id
            return data
        return None

    async def create(self, doc_id: str, data: Dict[str, Any]) -> None:
        self._col().document(doc_id).set(data)

    async def update(self, doc_id: str, data: Dict[str, Any]) -> None:
        self._col().document(doc_id).update(data)

    async def delete(self, doc_id: str) -> None:
        self._col().document(doc_id).delete()

    async def list(
        self,
        filters: Optional[List[tuple]] = None,
        order_by: Optional[str] = None,
        limit: Optional[int] = None,
    ) -> List[Dict[str, Any]]:
        """List documents with optional filtering, ordering, and limit."""
        query = self._col()

        if filters:
            for field, op, value in filters:
                query = query.where(field, op, value)

        if order_by:
            query = query.order_by(order_by)

        if limit:
            query = query.limit(limit)

        docs = query.stream()
        results = []
        for doc in docs:
            data = doc.to_dict()
            data["id"] = doc.id
            results.append(data)
        return results

    async def batch_create(self, items: List[Dict[str, Any]], id_field: str = "id") -> None:
        """Create multiple documents in a single Firestore batch."""
        batch = self._client().batch()
        for item in items:
            doc_id = item.get(id_field) or self._col().document().id
            doc_ref = self._col().document(str(doc_id))
            batch.set(doc_ref, item)
        batch.commit()
