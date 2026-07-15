from __future__ import annotations

from dataclasses import dataclass

from faker import Faker


fake = Faker()


@dataclass(frozen=True)
class FeedbackPayload:
    name: str
    message: str


@dataclass(frozen=True)
class BookingPayload:
    date: str
    time: str
    reason: str


def feedback_payload() -> FeedbackPayload:
    return FeedbackPayload(
        name=fake.name(),
        message="Appium E2E feedback smoke test.",
    )


def booking_payload() -> BookingPayload:
    return BookingPayload(
        date="2026-07-20",
        time="10:00 AM",
        reason="Routine TMD consultation from Appium E2E.",
    )
