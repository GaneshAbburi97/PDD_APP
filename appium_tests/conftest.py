from __future__ import annotations

from contextlib import contextmanager
from datetime import datetime
from pathlib import Path

import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options

from config import AppiumConfig, REPORTS_DIR, TestCredentials
from pages.auth_page import AuthPage
from utils.reporting import ExcelReportBuilder, TestResultCollector


def pytest_addoption(parser: pytest.Parser) -> None:
    parser.addoption(
        "--appium-server",
        action="store",
        default=None,
        help="Appium server URL. Defaults to APPIUM_SERVER_URL or http://127.0.0.1:4723.",
    )
    parser.addoption(
        "--device-name",
        action="store",
        default=None,
        help="Android device name capability. Defaults to APPIUM_DEVICE_NAME or Android.",
    )
    parser.addoption(
        "--apk",
        action="store",
        default=None,
        help="Optional APK path. Defaults to installed appPackage/appActivity launch.",
    )
    parser.addoption(
        "--report-dir",
        action="store",
        default=str(REPORTS_DIR),
        help="Folder where JSON, screenshots, and Excel reports are saved.",
    )


def pytest_configure(config: pytest.Config) -> None:
    report_dir = Path(config.getoption("--report-dir")).resolve()
    report_dir.mkdir(parents=True, exist_ok=True)
    (report_dir / "screenshots").mkdir(parents=True, exist_ok=True)
    config._tmd_report_collector = TestResultCollector(report_dir=report_dir)
    config._tmd_run_started_at = datetime.now()


@pytest.fixture(scope="session")
def appium_config(pytestconfig: pytest.Config) -> AppiumConfig:
    return AppiumConfig.from_env(
        server_url=pytestconfig.getoption("--appium-server"),
        device_name=pytestconfig.getoption("--device-name"),
        apk_path=pytestconfig.getoption("--apk"),
    )


@pytest.fixture(scope="session")
def test_credentials() -> TestCredentials:
    return TestCredentials.from_env()


@contextmanager
def appium_session(appium_config: AppiumConfig):
    options = UiAutomator2Options().load_capabilities(appium_config.capabilities())
    session = None
    try:
        session = webdriver.Remote(appium_config.server_url, options=options)
        session.implicitly_wait(appium_config.implicit_wait_seconds)
    except Exception as exc:
        pytest.fail(
            "Could not start an Appium Android session. "
            "Check that the Appium server is running, the UiAutomator2 driver is installed, "
            f"and the device/app are available. Root cause: {exc}"
        )
    try:
        yield session
    finally:
        if session is not None:
            session.quit()


@pytest.fixture
def driver(appium_config: AppiumConfig):
    with appium_session(appium_config) as session:
        yield session


@pytest.fixture
def authenticated_driver(test_credentials: TestCredentials, appium_config: AppiumConfig):
    if not test_credentials.available:
        pytest.skip("Set TMD_TEST_EMAIL and TMD_TEST_PASSWORD to run authenticated E2E flows.")
    with appium_session(appium_config) as driver:
        auth = AuthPage(driver, timeout=appium_config.explicit_wait_seconds)
        auth.login(test_credentials.email or "", test_credentials.password or "")
        yield driver


@pytest.fixture(scope="session")
def authenticated_session_driver(test_credentials: TestCredentials, appium_config: AppiumConfig):
    if not test_credentials.available:
        pytest.skip("Set TMD_TEST_EMAIL and TMD_TEST_PASSWORD to run authenticated E2E flows.")
    with appium_session(appium_config) as driver:
        auth = AuthPage(driver, timeout=appium_config.explicit_wait_seconds)
        auth.login(test_credentials.email or "", test_credentials.password or "")
        yield driver


@pytest.fixture
def logged_in_or_existing_driver(driver, test_credentials: TestCredentials, appium_config: AppiumConfig):
    auth = AuthPage(driver, timeout=appium_config.explicit_wait_seconds)
    if auth.is_dashboard_visible(timeout=5):
        yield driver
        return
    if not test_credentials.available:
        pytest.skip("Set TMD_TEST_EMAIL and TMD_TEST_PASSWORD to run authenticated E2E flows.")
    auth.login(test_credentials.email or "", test_credentials.password or "")
    yield driver


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item: pytest.Item, call: pytest.CallInfo):
    outcome = yield
    report = outcome.get_result()
    if report.when not in {"setup", "call"}:
        return
    if report.when == "setup" and not (report.failed or report.skipped):
        return
    if report.when == "call" and hasattr(item, "_tmd_report_recorded"):
        return

    driver_obj = (
        item.funcargs.get("driver")
        or item.funcargs.get("authenticated_driver")
        or item.funcargs.get("authenticated_session_driver")
        or item.funcargs.get("logged_in_or_existing_driver")
    )
    collector: TestResultCollector = item.config._tmd_report_collector
    screenshot_path = None
    if driver_obj is not None and report.failed:
        screenshot_path = collector.save_failure_screenshot(driver_obj, item.nodeid)

    collector.add_pytest_report(item, report, stage=report.when, screenshot_path=screenshot_path)
    item._tmd_report_recorded = True


def pytest_sessionfinish(session: pytest.Session, exitstatus: int) -> None:
    if getattr(session.config.option, "collectonly", False):
        return

    collector: TestResultCollector = session.config._tmd_report_collector
    run_started_at = session.config._tmd_run_started_at
    appium_cfg = AppiumConfig.from_env(
        server_url=session.config.getoption("--appium-server"),
        device_name=session.config.getoption("--device-name"),
        apk_path=session.config.getoption("--apk"),
    )
    paths = collector.flush()
    workbook_path = ExcelReportBuilder(
        results=collector.results,
        report_dir=collector.report_dir,
        run_started_at=run_started_at,
        appium_config=appium_cfg,
        raw_results_path=paths["json"],
    ).build()
    terminal_reporter = session.config.pluginmanager.get_plugin("terminalreporter")
    if terminal_reporter:
        terminal_reporter.write_sep("=", f"TMD Appium Excel report: {workbook_path}")
