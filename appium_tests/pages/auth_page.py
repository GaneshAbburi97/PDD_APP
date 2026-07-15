from __future__ import annotations

from pages.base_page import BasePage


class AuthPage(BasePage):
    LOGIN_SCREEN_TEXT = ("Welcome Back", "Email", "Password", "Login", "Login with Google", "Sign Up")
    DASHBOARD_TEXT = ("Your Care Hub", "Daily Progress", "TMD Care AI")

    def assert_login_screen(self) -> None:
        for text in self.LOGIN_SCREEN_TEXT:
            self.wait_for_text(text)

    def is_dashboard_visible(self, timeout: int = 3) -> bool:
        return any(self.is_text_visible(text, timeout=timeout) for text in self.DASHBOARD_TEXT)

    def login(self, email: str, password: str) -> None:
        if self.is_dashboard_visible(timeout=3):
            return
        self.wait_for_text("Welcome Back")
        self.type_into_field(0, email)
        self.type_into_field(1, password)
        self.hide_keyboard()
        self.tap_text("Login")
        self.wait_for_any_text(self.DASHBOARD_TEXT, timeout=25)

    def submit_empty_login(self) -> None:
        self.wait_for_text("Welcome Back")
        self.tap_text("Login")
        self.wait_for_text("Please fill in all fields")

    def open_signup(self) -> None:
        self.tap_text("Sign Up")
        self.wait_for_text("Create Account")

    def open_forgot_password(self) -> None:
        self.tap_text("Forgot Password?")
        self.wait_for_text("Reset Password")
