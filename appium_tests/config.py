from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path
from typing import Any


ROOT_DIR = Path(__file__).resolve().parent
REPORTS_DIR = ROOT_DIR / "reports"
SCREENSHOTS_DIR = REPORTS_DIR / "screenshots"


def _env_bool(name: str, default: bool) -> bool:
    raw = os.getenv(name)
    if raw is None:
        return default
    return raw.strip().lower() in {"1", "true", "yes", "y", "on"}


@dataclass(frozen=True)
class AppiumConfig:
    server_url: str
    platform_name: str
    automation_name: str
    device_name: str
    app_package: str
    app_activity: str
    apk_path: str | None
    udid: str | None
    platform_version: str | None
    no_reset: bool
    full_reset: bool
    auto_grant_permissions: bool
    implicit_wait_seconds: int
    explicit_wait_seconds: int
    new_command_timeout_seconds: int

    @classmethod
    def from_env(
        cls,
        *,
        server_url: str | None = None,
        device_name: str | None = None,
        apk_path: str | None = None,
    ) -> "AppiumConfig":
        return cls(
            server_url=server_url or os.getenv("APPIUM_SERVER_URL", "http://127.0.0.1:4723"),
            platform_name=os.getenv("APPIUM_PLATFORM_NAME", "Android"),
            automation_name=os.getenv("APPIUM_AUTOMATION_NAME", "UiAutomator2"),
            device_name=device_name or os.getenv("APPIUM_DEVICE_NAME", "Android"),
            app_package=os.getenv("TMD_APP_PACKAGE", "com.example.tmdapp"),
            app_activity=os.getenv("TMD_APP_ACTIVITY", ".MainActivity"),
            apk_path=apk_path or os.getenv("TMD_APK_PATH"),
            udid=os.getenv("APPIUM_UDID"),
            platform_version=os.getenv("APPIUM_PLATFORM_VERSION"),
            no_reset=_env_bool("APPIUM_NO_RESET", False),
            full_reset=_env_bool("APPIUM_FULL_RESET", False),
            auto_grant_permissions=_env_bool("APPIUM_AUTO_GRANT_PERMISSIONS", True),
            implicit_wait_seconds=int(os.getenv("APPIUM_IMPLICIT_WAIT", "1")),
            explicit_wait_seconds=int(os.getenv("APPIUM_EXPLICIT_WAIT", "15")),
            new_command_timeout_seconds=int(os.getenv("APPIUM_NEW_COMMAND_TIMEOUT", "180")),
        )

    def capabilities(self) -> dict[str, Any]:
        caps: dict[str, Any] = {
            "platformName": self.platform_name,
            "automationName": self.automation_name,
            "deviceName": self.device_name,
            "noReset": self.no_reset,
            "fullReset": self.full_reset,
            "autoGrantPermissions": self.auto_grant_permissions,
            "newCommandTimeout": self.new_command_timeout_seconds,
            "appWaitActivity": "*",
            "disableWindowAnimation": True,
            "ignoreUnimportantViews": True,
        }
        if self.udid:
            caps["udid"] = self.udid
        if self.platform_version:
            caps["platformVersion"] = self.platform_version
        if self.apk_path:
            caps["app"] = str(Path(self.apk_path).expanduser().resolve())
        else:
            caps["appPackage"] = self.app_package
            caps["appActivity"] = self.app_activity
        return caps


@dataclass(frozen=True)
class TestCredentials:
    email: str | None
    password: str | None

    @classmethod
    def from_env(cls) -> "TestCredentials":
        return cls(
            email=os.getenv("TMD_TEST_EMAIL"),
            password=os.getenv("TMD_TEST_PASSWORD"),
        )

    @property
    def available(self) -> bool:
        return bool(self.email and self.password)
