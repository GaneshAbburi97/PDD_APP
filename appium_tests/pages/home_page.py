from __future__ import annotations

from pages.base_page import BasePage


class HomePage(BasePage):
    def assert_dashboard(self) -> None:
        self.wait_for_text("TMD Care AI")
        self.wait_for_text("Your Care Hub")
        self.wait_for_text("Daily Progress")
        self.wait_for_text("Log Pain")
        self.wait_for_text("Start Exercises")

    def open_bottom_tab(self, label: str) -> None:
        try:
            self.tap_accessibility(label, timeout=5)
        except Exception:
            self.tap_text(label, timeout=5)

    def open_pain_map(self) -> None:
        self.open_bottom_tab("Pain Map")
        self.wait_for_text("Map Your Pain")

    def open_exercises(self) -> None:
        self.open_bottom_tab("Exercises")
        self.wait_for_text("Your Exercise Program")

    def open_reports(self) -> None:
        self.open_bottom_tab("Progress")
        self.wait_for_text("Trends")
        self.wait_for_text("Weekly")
        self.wait_for_text("Monthly")
        self.wait_for_text("Activity")

    def open_daily_wellness(self) -> None:
        self.scroll_and_tap_text("Daily Wellness")
        self.wait_for_text("Daily Wellness Check-In")

    def open_sleep_tracking(self) -> None:
        self.scroll_and_tap_text("Sleep Tracking")
        self.wait_for_text("Sleep Tracking")

    def open_ai_chat(self) -> None:
        self.tap_accessibility("AI Chat")
        self.wait_for_text("AI Health Assistant")

    def open_notifications(self) -> None:
        self.tap_accessibility("Notifications")
        self.wait_for_text("Notifications")

    def open_profile_from_header(self) -> None:
        width, _ = self.current_screen_size()
        # The profile avatar is the left header control and currently has no accessibility label.
        self.tap_coordinates(max(36, int(width * 0.11)), 58)
        self.wait_for_text("Edit Profile")
