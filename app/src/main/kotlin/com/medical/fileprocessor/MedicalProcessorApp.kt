package com.medical.fileprocessor

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main Application class
 * 
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation, 
 * including a base class for your application that serves as the 
 * application-level dependency container.
 */
@HiltAndroidApp
class MedicalProcessorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        setupLogging()
        Timber.tag("APP").i("🚀 Medical Processor App Initialized with Firebase")
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            // Debug mode: Log everything to Logcat
            Timber.plant(Timber.DebugTree())
        } else {
            // Release mode: Only log errors/warnings to crash reporting
            Timber.plant(CrashReportingTree())
        }
    }
}

/**
 * Custom Timber tree for production logging.
 * Filters logs to only send Warnings and Errors to a crash reporting service.
 */
class CrashReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if ((priority == Log.VERBOSE) || (priority == Log.DEBUG) || (priority == Log.INFO)) {
            return
        }

        // Production crash reporting would go here
        // e.g. FirebaseCrashlytics.getInstance().log(message)
    }
}
