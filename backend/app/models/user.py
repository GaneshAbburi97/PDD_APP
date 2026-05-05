"""Domain model: User."""

from __future__ import annotations

from datetime import datetime
from typing import Any, Dict, List, Optional


class User:
    def __init__(
        self,
        uid: str,
        email: Optional[str] = None,
        display_name: Optional[str] = None,
        roles: Optional[List[str]] = None,
        created_at: Optional[datetime] = None,
    ) -> None:
        self.uid = uid
        self.email = email
        self.display_name = display_name
        self.roles: List[str] = roles or ["user"]
        self.created_at = created_at or datetime.utcnow()

    def is_admin(self) -> bool:
        return "admin" in self.roles

    def to_dict(self) -> Dict[str, Any]:
        return {
            "uid": self.uid,
            "email": self.email,
            "display_name": self.display_name,
            "roles": self.roles,
            "created_at": self.created_at.isoformat(),
        }
