from __future__ import annotations

import pytest

from pages.home_page import HomePage
from pages.tracking_page import ExercisePage, PainMapPage, SleepPage, WellnessPage


@pytest.mark.e2e
@pytest.mark.tracking
@pytest.mark.requires_credentials
def test_pain_map_region_can_be_saved(authenticated_driver, appium_config):
    home = HomePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    home.open_pain_map()

    pain_map = PainMapPage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    pain_map.save_location(region="Right Jaw")


@pytest.mark.e2e
@pytest.mark.tracking
@pytest.mark.requires_credentials
def test_daily_wellness_and_sleep_entries_can_be_saved(authenticated_driver, appium_config):
    home = HomePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    home.assert_dashboard()

    home.open_daily_wellness()
    WellnessPage(authenticated_driver, timeout=appium_config.explicit_wait_seconds).save_default_entry()
    home.press_back()

    home.open_sleep_tracking()
    SleepPage(authenticated_driver, timeout=appium_config.explicit_wait_seconds).save_default_entry()


@pytest.mark.e2e
@pytest.mark.tracking
@pytest.mark.requires_credentials
def test_exercise_program_is_available(authenticated_driver, appium_config):
    home = HomePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    home.open_exercises()

    exercise = ExercisePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    exercise.assert_loaded()
