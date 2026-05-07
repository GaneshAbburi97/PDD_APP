package com.medical.fileprocessor.util;

import com.medical.fileprocessor.BuildConfig;

/**
 * Object containing all app constants.
 *
 * Environment selection via BuildConfig.BACKEND_ENV:
 * - debug -> EMULATOR (http://10.0.2.2:8000)
 * - release -> PROD (https://us-central1-...)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\u001c\n\u0002\u0010 \n\u0002\b\u0010\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u0002IJB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u001eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\'\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020\u001eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00102\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00105\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00106\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00107\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00108\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u00109\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0017\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00040;\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010=R\u0017\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00040;\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010=R\u000e\u0010@\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010A\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010B\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010C\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010D\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010E\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010F\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010G\u001a\u00020\u001eX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010H\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006K"}, d2 = {"Lcom/medical/fileprocessor/util/Constants;", "", "()V", "BASE_URL", "", "getBASE_URL", "()Ljava/lang/String;", "CURRENT_ENVIRONMENT", "Lcom/medical/fileprocessor/util/Constants$ApiEnvironment;", "DEFAULT_CLOUD_PROVIDER", "Lcom/medical/fileprocessor/util/Constants$CloudProvider;", "getDEFAULT_CLOUD_PROVIDER", "()Lcom/medical/fileprocessor/util/Constants$CloudProvider;", "ERROR_EMAIL_ALREADY_EXISTS", "ERROR_FILE_TOO_LARGE", "ERROR_INVALID_CREDENTIALS", "ERROR_INVALID_FILE", "ERROR_NO_INTERNET", "ERROR_PROCESSING_FAILED", "ERROR_UPLOAD_FAILED", "ERROR_WEAK_PASSWORD", "ESTIMATED_PROCESSING_TIME_SECONDS", "", "FIREBASE_API_BASE_URL", "FIREBASE_PROJECT_ID", "FIREBASE_STORAGE_BUCKET", "FIRESTORE_DATABASE_ID", "LOCAL_DEVICE_API_BASE_URL", "LOCAL_EMULATOR_API_BASE_URL", "MAX_FILE_SIZE_MB", "", "MAX_POLLING_ATTEMPTS", "MIN_PASSWORD_DIGITS", "MIN_PASSWORD_LENGTH", "MIN_PASSWORD_LOWERCASE", "MIN_PASSWORD_UPPERCASE", "POLLING_INTERVAL_MS", "PREF_AUTH_TOKEN", "PREF_CLOUD_PROVIDER", "PREF_DARK_MODE", "PREF_LAST_FILE_PATH", "PREF_NAME", "PREF_USER_DISPLAY_NAME", "PREF_USER_EMAIL", "PREF_USER_ID", "PROCESSING_TIMEOUT_MS", "ROUTE_HISTORY", "ROUTE_LOGIN", "ROUTE_PROCESSING", "ROUTE_REGISTER", "ROUTE_RESULT", "ROUTE_SETTINGS", "ROUTE_UPLOAD", "SUCCESS_LOGIN", "SUCCESS_PROCESSING", "SUCCESS_PROCESSING_COMPLETE", "SUCCESS_REGISTER", "SUCCESS_UPLOAD", "SUPPORTED_FILE_EXTENSIONS", "", "getSUPPORTED_FILE_EXTENSIONS", "()Ljava/util/List;", "SUPPORTED_MIME_TYPES", "getSUPPORTED_MIME_TYPES", "TAG_AUTH", "TAG_DATABASE", "TAG_NETWORK", "TAG_PROCESSING", "TAG_STORAGE", "TAG_UI", "TAG_UPLOAD", "TOKEN_REFRESH_INTERVAL_MS", "UPLOAD_CHUNK_SIZE", "ApiEnvironment", "CloudProvider", "app_debug"})
public final class Constants {
    
    /**
     * Current active cloud provider.
     */
    @org.jetbrains.annotations.NotNull()
    private static final com.medical.fileprocessor.util.Constants.CloudProvider DEFAULT_CLOUD_PROVIDER = com.medical.fileprocessor.util.Constants.CloudProvider.FIREBASE;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FIREBASE_API_BASE_URL = "https://us-central1-medical-processor.cloudfunctions.net";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LOCAL_EMULATOR_API_BASE_URL = "http://10.0.2.2:8000";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LOCAL_DEVICE_API_BASE_URL = "http://192.168.1.100:8000";
    
    /**
     * Determine environment from BuildConfig.BACKEND_ENV
     *
     * SAFETY:
     * - BuildConfig field injected by build system during compilation
     * - Default to PROD for production builds
     */
    @org.jetbrains.annotations.NotNull()
    private static final com.medical.fileprocessor.util.Constants.ApiEnvironment CURRENT_ENVIRONMENT = null;
    
    /**
     * Base URL for Retrofit - automatically switches based on build environment
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String BASE_URL = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FIREBASE_PROJECT_ID = "medical-processor-prod";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FIREBASE_STORAGE_BUCKET = "medical-processor-prod.appspot.com";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FIRESTORE_DATABASE_ID = "(default)";
    public static final long MAX_FILE_SIZE_MB = 500L;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> SUPPORTED_FILE_EXTENSIONS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> SUPPORTED_MIME_TYPES = null;
    public static final int UPLOAD_CHUNK_SIZE = 5242880;
    public static final long POLLING_INTERVAL_MS = 2000L;
    public static final int MAX_POLLING_ATTEMPTS = 1800;
    public static final long PROCESSING_TIMEOUT_MS = 3600000L;
    public static final int ESTIMATED_PROCESSING_TIME_SECONDS = 120;
    public static final long TOKEN_REFRESH_INTERVAL_MS = 900000L;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_PASSWORD_UPPERCASE = 1;
    public static final int MIN_PASSWORD_LOWERCASE = 1;
    public static final int MIN_PASSWORD_DIGITS = 1;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ROUTE_LOGIN = "login";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ROUTE_REGISTER = "register";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ROUTE_UPLOAD = "upload";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ROUTE_PROCESSING = "processing/{jobId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ROUTE_RESULT = "result/{jobId}";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ROUTE_SETTINGS = "settings";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ROUTE_HISTORY = "history";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_NAME = "medical_processor_prefs";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_USER_ID = "user_id";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_AUTH_TOKEN = "auth_token";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_CLOUD_PROVIDER = "cloud_provider";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_USER_EMAIL = "user_email";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_USER_DISPLAY_NAME = "user_display_name";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_LAST_FILE_PATH = "last_file_path";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PREF_DARK_MODE = "dark_mode";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ERROR_NO_INTERNET = "No internet connection";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ERROR_INVALID_FILE = "Invalid file format. Please use .nii or .nii.gz";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ERROR_FILE_TOO_LARGE = "File size exceeds 50 MB limit";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ERROR_UPLOAD_FAILED = "File upload failed. Please try again";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ERROR_PROCESSING_FAILED = "Processing failed. Please try again";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ERROR_INVALID_CREDENTIALS = "Invalid email or password";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ERROR_EMAIL_ALREADY_EXISTS = "Email already registered";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ERROR_WEAK_PASSWORD = "Password must be at least 8 characters with uppercase, lowercase, and numbers";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUCCESS_LOGIN = "Login successful";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUCCESS_REGISTER = "Registration successful";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUCCESS_UPLOAD = "File uploaded successfully";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUCCESS_PROCESSING = "Processing started";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SUCCESS_PROCESSING_COMPLETE = "Processing complete";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_AUTH = "AUTH";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_UPLOAD = "UPLOAD";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_NETWORK = "NETWORK";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_PROCESSING = "PROCESSING";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_DATABASE = "DATABASE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_STORAGE = "STORAGE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String TAG_UI = "UI";
    @org.jetbrains.annotations.NotNull()
    public static final com.medical.fileprocessor.util.Constants INSTANCE = null;
    
    private Constants() {
        super();
    }
    
    /**
     * Current active cloud provider.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.medical.fileprocessor.util.Constants.CloudProvider getDEFAULT_CLOUD_PROVIDER() {
        return null;
    }
    
    /**
     * Base URL for Retrofit - automatically switches based on build environment
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBASE_URL() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getSUPPORTED_FILE_EXTENSIONS() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getSUPPORTED_MIME_TYPES() {
        return null;
    }
    
    /**
     * Environment selection for API.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/medical/fileprocessor/util/Constants$ApiEnvironment;", "", "(Ljava/lang/String;I)V", "PROD", "EMULATOR", "DEVICE", "app_debug"})
    public static enum ApiEnvironment {
        /*public static final*/ PROD /* = new PROD() */,
        /*public static final*/ EMULATOR /* = new EMULATOR() */,
        /*public static final*/ DEVICE /* = new DEVICE() */;
        
        ApiEnvironment() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.medical.fileprocessor.util.Constants.ApiEnvironment> getEntries() {
            return null;
        }
    }
    
    /**
     * Available cloud providers.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0003\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/medical/fileprocessor/util/Constants$CloudProvider;", "", "(Ljava/lang/String;I)V", "FIREBASE", "app_debug"})
    public static enum CloudProvider {
        /*public static final*/ FIREBASE /* = new FIREBASE() */;
        
        CloudProvider() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.medical.fileprocessor.util.Constants.CloudProvider> getEntries() {
            return null;
        }
    }
}