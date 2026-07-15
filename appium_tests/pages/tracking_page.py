from __future__ import annotations

from pages.base_page import BasePage


class PainMapPage(BasePage):
    def assert_loaded(self) -> None:
        self.wait_for_text("Map Your Pain")
        self.wait_for_text("QUICK SELECTION")
        self.wait_for_text("INTENSITY")
        self.wait_for_text("STRESS LEVEL")

    def save_location(self, region: str = "Right Jaw") -> None:
        self.assert_loaded()
        self.tap_text(region)
        self.scroll_and_tap_text("Save Location")
        self.wait_for_text("Location and levels saved successfully!", timeout=8)


class WellnessPage(BasePage):
    def save_default_entry(self) -> None:
        self.wait_for_text("Daily Wellness Check-In")
        self.scroll_and_tap_text("Save Wellness Entry")
        self.wait_for_text("Wellness entry saved successfully!", timeout=8)


class SleepPage(BasePage):
    def save_default_entry(self) -> None:
        self.wait_for_text("Sleep Tracking")
        self.scroll_and_tap_text("Save Sleep Entry")
        self.wait_for_text("Sleep entry saved successfully!", timeout=8)


class ExercisePage(BasePage):
    def assert_loaded(self) -> None:
        self.dismiss_severe_alert_if_present()
        self.wait_for_text("Your Exercise Program")
        self.wait_for_text("Start", timeout=8)

    def dismiss_severe_alert_if_present(self) -> None:
        if self.is_text_visible("Severe Pain Alert", timeout=2):
            self.tap_text("Continue Carefully")


class ReportsPage(BasePage):
    def assert_loaded(self) -> None:
        for tab in ("Trends", "Weekly", "Monthly", "Activity"):
            self.wait_for_text(tab)
        self.wait_for_text("Health Report")

    def open_health_report(self) -> None:
        self.tap_text("Health Report")
        self.wait_for_text("Health Reports")
