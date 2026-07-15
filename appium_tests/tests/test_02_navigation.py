from __future__ import annotations

import pytest

from pages.home_page import HomePage
from pages.tracking_page import ExercisePage, PainMapPage, ReportsPage


@pytest.mark.e2e
@pytest.mark.navigation
@pytest.mark.requires_credentials
def test_authenticated_dashboard_and_bottom_navigation(authenticated_driver, appium_config):
    home = HomePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    home.assert_dashboard()

    home.open_pain_map()
    PainMapPage(authenticated_driver, timeout=appium_config.explicit_wait_seconds).assert_loaded()

    home.open_exercises()
    ExercisePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds).assert_loaded()

    home.open_reports()
    ReportsPage(authenticated_driver, timeout=appium_config.explicit_wait_seconds).assert_loaded()

    home.open_bottom_tab("Dashboard")
    home.assert_dashboard()


@pytest.mark.e2e
@pytest.mark.navigation
@pytest.mark.requires_credentials
def test_dashboard_shortcuts_open_secondary_screens(authenticated_driver, appium_config):
    home = HomePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    home.assert_dashboard()

    home.open_ai_chat()
    home.wait_for_text("Type your message...")
    home.press_back()

    home.open_notifications()
    home.wait_for_text("Time for Exercise")
    home.press_back()

    home.open_daily_wellness()
    home.wait_for_text("Daily Wellness Check-In")
    home.press_back()

    home.open_sleep_tracking()
    home.wait_for_text("Better sleep supports jaw recovery", contains=True)
