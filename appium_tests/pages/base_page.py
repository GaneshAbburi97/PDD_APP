from __future__ import annotations

from collections.abc import Iterable

from appium.webdriver.common.appiumby import AppiumBy
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.support import expected_conditions as ec
from selenium.webdriver.support.ui import WebDriverWait


def _quote_ui_text(text: str) -> str:
    escaped = text.replace("\\", "\\\\").replace('"', '\\"')
    return f'"{escaped}"'


class BasePage:
    def __init__(self, driver, timeout: int = 15):
        self.driver = driver
        self.timeout = timeout

    def wait(self, timeout: int | None = None) -> WebDriverWait:
        return WebDriverWait(self.driver, timeout or self.timeout)

    def by_text(self, text: str, *, contains: bool = False) -> tuple[str, str]:
        method = "textContains" if contains else "text"
        return (
            AppiumBy.ANDROID_UIAUTOMATOR,
            f"new UiSelector().{method}({_quote_ui_text(text)})",
        )

    def by_accessibility_id(self, label: str) -> tuple[str, str]:
        return (AppiumBy.ACCESSIBILITY_ID, label)

    def find_text(self, text: str, *, contains: bool = False, timeout: int | None = None):
        return self.wait(timeout).until(ec.presence_of_element_located(self.by_text(text, contains=contains)))

    def wait_for_text(self, text: str, *, contains: bool = False, timeout: int | None = None):
        return self.wait(timeout).until(ec.visibility_of_element_located(self.by_text(text, contains=contains)))

    def wait_for_any_text(self, texts: Iterable[str], *, timeout: int | None = None):
        last_error: Exception | None = None
        for text in texts:
            try:
                return self.wait_for_text(text, timeout=timeout or 2)
            except Exception as exc:
                last_error = exc
        raise AssertionError(f"None of these texts were visible: {', '.join(texts)}") from last_error

    def is_text_visible(self, text: str, *, contains: bool = False, timeout: int = 2) -> bool:
        try:
            self.wait_for_text(text, contains=contains, timeout=timeout)
            return True
        except TimeoutException:
            return False

    def tap_text(self, text: str, *, contains: bool = False, timeout: int | None = None) -> None:
        self.wait(timeout).until(ec.element_to_be_clickable(self.by_text(text, contains=contains))).click()

    def tap_accessibility(self, label: str, *, timeout: int | None = None) -> None:
        self.wait(timeout).until(ec.element_to_be_clickable(self.by_accessibility_id(label))).click()

    def tap_first_available_text(self, *texts: str, timeout: int = 3) -> str:
        for text in texts:
            try:
                self.tap_text(text, timeout=timeout)
                return text
            except Exception:
                continue
        raise AssertionError(f"Could not tap any of these texts: {', '.join(texts)}")

    def edit_text_fields(self):
        fields = self.driver.find_elements(AppiumBy.CLASS_NAME, "android.widget.EditText")
        if not fields:
            fields = self.driver.find_elements(AppiumBy.XPATH, "//*[@class='android.widget.EditText']")
        return fields

    def type_into_field(self, index: int, value: str, *, clear: bool = True) -> None:
        fields = self.edit_text_fields()
        if index >= len(fields):
            raise AssertionError(f"Expected text field index {index}, but only found {len(fields)} fields.")
        field = fields[index]
        field.click()
        if clear:
            try:
                field.clear()
            except Exception:
                pass
        field.send_keys(value)

    def hide_keyboard(self) -> None:
        try:
            self.driver.hide_keyboard()
        except Exception:
            pass

    def scroll_to_text(self, text: str):
        selector = (
            "new UiScrollable(new UiSelector().scrollable(true))"
            f".scrollIntoView(new UiSelector().text({_quote_ui_text(text)}))"
        )
        return self.driver.find_element(AppiumBy.ANDROID_UIAUTOMATOR, selector)

    def scroll_and_tap_text(self, text: str) -> None:
        try:
            self.scroll_to_text(text).click()
        except Exception:
            self.tap_text(text, timeout=3)

    def press_back(self) -> None:
        self.driver.back()

    def tap_coordinates(self, x: int, y: int) -> None:
        self.driver.execute_script("mobile: clickGesture", {"x": x, "y": y})

    def current_screen_size(self) -> tuple[int, int]:
        size = self.driver.get_window_size()
        return int(size["width"]), int(size["height"])
