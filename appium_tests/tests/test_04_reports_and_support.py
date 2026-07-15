from __future__ import annotations

import pytest

from pages.home_page import HomePage
from pages.support_page import DoctorsPage, ProfilePage
from pages.tracking_page import ReportsPage


@pytest.mark.e2e
@pytest.mark.reports
@pytest.mark.requires_credentials
def test_progress_tabs_and_health_report_are_reachable(authenticated_driver, appium_config):
    home = HomePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    home.open_reports()

    reports = ReportsPage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    reports.assert_loaded()
    reports.open_health_report()
    reports.wait_for_text("Overall Recovery Score")
    reports.scroll_to_text("1. PAIN & STRESS REPORT")
    reports.wait_for_text("1. PAIN & STRESS REPORT")


@pytest.mark.e2e
@pytest.mark.support
@pytest.mark.requires_credentials
def test_profile_support_and_doctor_booking_are_reachable(authenticated_driver, appium_config):
    home = HomePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    home.assert_dashboard()
    home.open_profile_from_header()

    profile = ProfilePage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    profile.assert_loaded()
    profile.open_doctors()

    doctors = DoctorsPage(authenticated_driver, timeout=appium_config.explicit_wait_seconds)
    doctors.assert_loaded()
    doctors.open_first_booking()
    doctors.wait_for_text("Date (e.g., YYYY-MM-DD)")
    doctors.wait_for_text("Reason for visit")
