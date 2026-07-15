from __future__ import annotations

import pytest

from pages.auth_page import AuthPage


@pytest.mark.auth
def test_login_screen_shows_required_controls(driver, appium_config):
    auth = AuthPage(driver, timeout=appium_config.explicit_wait_seconds)
    auth.assert_login_screen()


@pytest.mark.auth
def test_login_requires_email_and_password(driver, appium_config):
    auth = AuthPage(driver, timeout=appium_config.explicit_wait_seconds)
    auth.submit_empty_login()


@pytest.mark.auth
def test_signup_screen_is_reachable(driver, appium_config):
    auth = AuthPage(driver, timeout=appium_config.explicit_wait_seconds)
    auth.open_signup()
    for text in ("Full Name", "Email Address", "Password", "Confirm Password", "Create Account", "Sign Up with Google"):
        auth.wait_for_text(text)


@pytest.mark.auth
def test_forgot_password_screen_is_reachable(driver, appium_config):
    auth = AuthPage(driver, timeout=appium_config.explicit_wait_seconds)
    auth.open_forgot_password()
    for text in ("Reset Password", "Email Address", "Send Reset Link"):
        auth.wait_for_text(text)
