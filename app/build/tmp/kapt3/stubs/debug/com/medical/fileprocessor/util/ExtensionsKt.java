package com.medical.fileprocessor.util;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.util.Locale;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\"\n\u0000\n\u0002\u0010\u000e\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\u0014\u0010\u0003\u001a\u0004\u0018\u00010\u0001*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u001a\u0012\u0010\u0007\u001a\u00020\u0002*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u001a\n\u0010\b\u001a\u00020\t*\u00020\u0001\u001a\n\u0010\n\u001a\u00020\t*\u00020\u0001\u00a8\u0006\u000b"}, d2 = {"formatBytes", "", "", "getFileName", "Landroid/net/Uri;", "context", "Landroid/content/Context;", "getFileSize", "isStrongPassword", "", "isValidEmail", "app_debug"})
public final class ExtensionsKt {
    
    /**
     * Format bytes to human readable format
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String formatBytes(long $this$formatBytes) {
        return null;
    }
    
    /**
     * Validate email format using regex
     */
    public static final boolean isValidEmail(@org.jetbrains.annotations.NotNull()
    java.lang.String $this$isValidEmail) {
        return false;
    }
    
    /**
     * Validate password strength
     */
    public static final boolean isStrongPassword(@org.jetbrains.annotations.NotNull()
    java.lang.String $this$isStrongPassword) {
        return false;
    }
    
    /**
     * Get file name from URI
     */
    @org.jetbrains.annotations.Nullable()
    public static final java.lang.String getFileName(@org.jetbrains.annotations.NotNull()
    android.net.Uri $this$getFileName, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Get file size from URI in bytes
     */
    public static final long getFileSize(@org.jetbrains.annotations.NotNull()
    android.net.Uri $this$getFileSize, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0L;
    }
}