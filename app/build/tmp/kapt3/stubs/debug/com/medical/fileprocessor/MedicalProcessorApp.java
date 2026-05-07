package com.medical.fileprocessor;

import android.app.Application;
import android.util.Log;
import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

/**
 * Main Application class
 *
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation, 
 * including a base class for your application that serves as the 
 * application-level dependency container.
 */
@dagger.hilt.android.HiltAndroidApp()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0004H\u0002\u00a8\u0006\u0006"}, d2 = {"Lcom/medical/fileprocessor/MedicalProcessorApp;", "Landroid/app/Application;", "()V", "onCreate", "", "setupLogging", "app_debug"})
public final class MedicalProcessorApp extends android.app.Application {
    
    public MedicalProcessorApp() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    private final void setupLogging() {
    }
}