from __future__ import annotations

from pages.base_page import BasePage


class ProfilePage(BasePage):
    def assert_loaded(self) -> None:
        self.wait_for_text("Edit Profile")
        self.wait_for_text("SETTINGS")
        self.wait_for_text("REPORTS")

    def open_doctors(self) -> None:
        self.scroll_and_tap_text("Find a Doctor")
        self.wait_for_text("Consult Doctors")

    def open_feedback(self) -> None:
        self.scroll_and_tap_text("Feedback Form")
        self.wait_for_text("Feedback Form")

    def open_privacy_policy(self) -> None:
        self.scroll_and_tap_text("Privacy Policy")
        self.wait_for_text("Privacy Policy")


class DoctorsPage(BasePage):
    def assert_loaded(self) -> None:
        self.wait_for_text("Consult Doctors")
        self.wait_for_text("Dr. Ravi Kumar", timeout=8)
        self.wait_for_text("Book")

    def open_first_booking(self) -> None:
        self.tap_text("Book")
        self.wait_for_text("Book Appointment")
