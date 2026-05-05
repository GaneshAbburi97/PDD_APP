// File: app/src/main/kotlin/com/medical/fileprocessor/MainActivity.kt
// 📌 Main Activity - Entry point of the app
// 📌 Sets up Compose UI
// 📌 Initializes navigation
// 📌 Decorated with @AndroidEntryPoint for Hilt

package com.medical.fileprocessor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.medical.fileprocessor.ui.navigation.NavGraph
import com.medical.fileprocessor.ui.theme.MedicalFileProcessorTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Main Activity - The first screen the user sees
 *
 * LIFECYCLE:
 * 1. onCreate() - Activity created, UI initialized
 * 2. onStart() - Activity becoming visible
 * 3. onResume() - Activity visible and interactive
 * 4. onPause() - User leaves (press home, call, etc.)
 * 5. onStop() - Activity no longer visible
 * 6. onDestroy() - Activity destroyed (cleanup)
 *
 * @AndroidEntryPoint enables Hilt dependency injection for this Activity
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is first created
     *
     * TIMING: Once per activity lifetime
     *
     * @param savedInstanceState Previous state (if activity was killed and recreated)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Timber.tag("ACTIVITY").d("📱 MainActivity.onCreate() called")

        // Final UI confirmation: Ensure setContent is actually invoked with a non-empty container
        setContent {
            MedicalFileProcessorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background,
                ) {
                    NavGraph()
                }
            }
        }
    }

    /**
     * Called when the activity becomes visible to the user
     * AFTER onCreate()
     *
     * TIMING: Can be called multiple times (resume after pause)
     *
     * USE FOR:
     * - Resume background tasks
     * - Start sensors
     * - Request location updates
     */
    override fun onStart() {
        super.onStart()
        Timber.tag("ACTIVITY").d("📱 MainActivity.onStart() called - becoming visible")
    }

    /**
     * Called when the activity is in the foreground and interactive
     *
     * TIMING: Can be called multiple times (resume after pause)
     *
     * USE FOR:
     * - Resume background tasks
     * - Update UI
     * - Start animations
     */
    override fun onResume() {
        super.onResume()
        Timber.tag("ACTIVITY").d("📱 MainActivity.onResume() called - now interactive")
    }

    /**
     * Called when the user leaves the activity (press Home, receiving a call, etc.)
     * The activity is still visible but not interactive
     *
     * TIMING: Can be called multiple times (pause and resume)
     *
     * USE FOR:
     * - Pause background tasks
     * - Stop sensors
     * - Pause animations/music
     */
    override fun onPause() {
        super.onPause()
        Timber.tag("ACTIVITY").d("📱 MainActivity.onPause() called - no longer interactive")
    }

    /**
     * Called when the activity is no longer visible
     *
     * TIMING: Can be called multiple times
     *
     * USE FOR:
     * - Stop background services
     * - Save state
     * - Close connections
     */
    override fun onStop() {
        super.onStop()
        Timber.tag("ACTIVITY").d("📱 MainActivity.onStop() called - no longer visible")
    }

    /**
     * Called when the activity is destroyed
     * Final cleanup before the activity is garbage collected
     *
     * TIMING: Once per activity destruction (if not recreated)
     *
     * USE FOR:
     * - Final cleanup
     * - Release resources
     * - Stop threads
     */
    override fun onDestroy() {
        super.onDestroy()
        Timber.tag("ACTIVITY").d("📱 MainActivity.onDestroy() called - being destroyed")
    }

    /**
     * Called when the activity needs to save its state
     * Called before onStop() when the system might kill the process
     *
     * @param outState Bundle to save state in
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.tag("ACTIVITY").d("💾 MainActivity.onSaveInstanceState() - saving state")
    }

    /**
     * Called when the activity is recreated after being destroyed
     * Restore state from a saved instance
     *
     * @param savedInstanceState Bundle with saved state
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Timber.tag("ACTIVITY").d("💾 MainActivity.onRestoreInstanceState() - restoring state")
    }
}