package com.medical.fileprocessor.util

/**
 * Object containing all app constants.
 */
object Constants {

    // =========================================================================
    // CLOUD PROVIDER SELECTION
    // =========================================================================

    /**
     * Available cloud providers.
     */
    enum class CloudProvider {
        FIREBASE,
    }

    /**
     * Current active cloud provider.
     */
    val DEFAULT_CLOUD_PROVIDER = CloudProvider.FIREBASE

    // =========================================================================
    // API ENDPOINTS
    // =========================================================================

    const val FIREBASE_API_BASE_URL = "https://us-central1-medical-processor.cloudfunctions.net"
    const val LOCAL_API_BASE_URL = "http://10.0.2.2:8000"

    // =========================================================================
    // FIREBASE CONFIGURATION
    // =========================================================================

    const val FIREBASE_PROJECT_ID = "medical-processor-prod"
    const val FIREBASE_STORAGE_BUCKET = "medical-processor-prod.appspot.com"
    const val FIRESTORE_DATABASE_ID = "(default)"

    // =========================================================================
    // FILE UPLOAD CONFIGURATION
    // =========================================================================

    const val MAX_FILE_SIZE_MB = 500L

    val SUPPORTED_FILE_EXTENSIONS = listOf(
        "nii",
        "nii.gz",
    )

    val SUPPORTED_MIME_TYPES = listOf(
        "application/octet-stream",
        "application/gzip",
        "application/x-gzip",
        "image/nii",
    )

    const val UPLOAD_CHUNK_SIZE = 5 * 1024 * 1024 // 5 MB

    // =========================================================================
    // PROCESSING CONFIGURATION
    // =========================================================================

    const val POLLING_INTERVAL_MS = 2000L
    const val MAX_POLLING_ATTEMPTS = 1800
    const val PROCESSING_TIMEOUT_MS = 3600000L
    const val ESTIMATED_PROCESSING_TIME_SECONDS = 120

    // =========================================================================
    // AUTHENTICATION CONFIGURATION
    // =========================================================================

    const val TOKEN_REFRESH_INTERVAL_MS = 15 * 60 * 1000L
    const val MIN_PASSWORD_LENGTH = 8
    const val MIN_PASSWORD_UPPERCASE = 1
    const val MIN_PASSWORD_LOWERCASE = 1
    const val MIN_PASSWORD_DIGITS = 1

    // =========================================================================
    // NAVIGATION ROUTES
    // =========================================================================

    const val ROUTE_LOGIN = "login"
    const val ROUTE_REGISTER = "register"
    const val ROUTE_UPLOAD = "upload"
    const val ROUTE_PROCESSING = "processing/{jobId}"
    const val ROUTE_RESULT = "result/{jobId}"
    const val ROUTE_SETTINGS = "settings"
    const val ROUTE_HISTORY = "history"

    // =========================================================================
    // SHARED PREFERENCES KEYS
    // =========================================================================

    const val PREF_NAME = "medical_processor_prefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_AUTH_TOKEN = "auth_token"
    const val PREF_CLOUD_PROVIDER = "cloud_provider"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_USER_DISPLAY_NAME = "user_display_name"
    const val PREF_LAST_FILE_PATH = "last_file_path"
    const val PREF_DARK_MODE = "dark_mode"

    // =========================================================================
    // ERROR MESSAGES
    // =========================================================================

    const val ERROR_NO_INTERNET = "No internet connection"
    const val ERROR_INVALID_FILE = "Invalid file format. Please use .nii or .nii.gz"
    const val ERROR_FILE_TOO_LARGE = "File size exceeds 500 MB limit"
    const val ERROR_UPLOAD_FAILED = "File upload failed. Please try again"
    const val ERROR_PROCESSING_FAILED = "Processing failed. Please try again"
    const val ERROR_INVALID_CREDENTIALS = "Invalid email or password"
    const val ERROR_EMAIL_ALREADY_EXISTS = "Email already registered"
    const val ERROR_WEAK_PASSWORD = "Password must be at least 8 characters with uppercase, lowercase, and numbers"

    // =========================================================================
    // SUCCESS MESSAGES
    // =========================================================================

    const val SUCCESS_LOGIN = "Login successful"
    const val SUCCESS_REGISTER = "Registration successful"
    const val SUCCESS_UPLOAD = "File uploaded successfully"
    const val SUCCESS_PROCESSING = "Processing started"
    const val SUCCESS_PROCESSING_COMPLETE = "Processing complete"

    // =========================================================================
    // LOGGING TAGS
    // =========================================================================

    const val TAG_AUTH = "AUTH"
    const val TAG_UPLOAD = "UPLOAD"
    const val TAG_NETWORK = "NETWORK"
    const val TAG_PROCESSING = "PROCESSING"
    const val TAG_DATABASE = "DATABASE"
    const val TAG_STORAGE = "STORAGE"
    const val TAG_UI = "UI"
}
