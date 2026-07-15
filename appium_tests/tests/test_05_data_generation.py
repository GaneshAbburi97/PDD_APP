from __future__ import annotations
import pytest
import random
from faker import Faker
from pages.home_page import HomePage
from pages.tracking_page import WellnessPage, PainMapPage, SleepPage, ExercisePage, ReportsPage

fake = Faker()

from pages.auth_page import AuthPage

# Generate a list of remaining tasks (e.g. 1 to 301) to run 300 tests
FLOWS = ["pain_map", "wellness", "sleep", "exercises", "reports"]
# Use random.choices to guarantee random selection
TEST_CASES = [f"{random.choice(FLOWS)}_{i}" for i in range(1, 301)]

@pytest.mark.e2e
@pytest.mark.tracking
@pytest.mark.requires_credentials
@pytest.mark.parametrize("flow_task", TEST_CASES)
def test_comprehensive_app_flows(authenticated_session_driver, appium_config, flow_task):
    """
    Executes 300 randomized test flows covering all major app features.
    If an individual iteration fails, it recovers gracefully using the back button.
    """
    home = HomePage(authenticated_session_driver, timeout=appium_config.explicit_wait_seconds)
    
    flow = flow_task.rsplit('_', 1)[0]
    print(f"\\n--- Executing {flow} ---")
    
    try:
        home.assert_dashboard()
        
        if flow == "pain_map":
            home.open_pain_map()
            region = random.choice(["Left Jaw", "Right Jaw", "Neck", "Temples"])
            PainMapPage(authenticated_session_driver, timeout=appium_config.explicit_wait_seconds).save_location(region=region)
            home.open_bottom_tab("Dashboard")
            
        elif flow == "wellness":
            home.open_daily_wellness()
            WellnessPage(authenticated_session_driver, timeout=appium_config.explicit_wait_seconds).save_default_entry()
            home.press_back()
            
        elif flow == "sleep":
            home.open_sleep_tracking()
            SleepPage(authenticated_session_driver, timeout=appium_config.explicit_wait_seconds).save_default_entry()
            home.press_back()
            
        elif flow == "exercises":
            home.open_exercises()
            ExercisePage(authenticated_session_driver, timeout=appium_config.explicit_wait_seconds).assert_loaded()
            home.open_bottom_tab("Dashboard")
            
        elif flow == "reports":
            home.open_reports()
            ReportsPage(authenticated_session_driver, timeout=appium_config.explicit_wait_seconds).assert_loaded()
            home.open_bottom_tab("Dashboard")
            
    except Exception as e:
        print(f"FAILED: {e}")
        # Robust recovery: Reset the app state so the next iteration starts cleanly
        try:
            authenticated_session_driver.terminate_app('com.example.tmdapp')
            authenticated_session_driver.activate_app('com.example.tmdapp')
            import os
            email = os.environ.get("TMD_TEST_EMAIL", "test@example.com")
            password = os.environ.get("TMD_TEST_PASSWORD", "123456")
            AuthPage(authenticated_session_driver, timeout=appium_config.explicit_wait_seconds).login(email, password)
        except:
            pass
        raise e
