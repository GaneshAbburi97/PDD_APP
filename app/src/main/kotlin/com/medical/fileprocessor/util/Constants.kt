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
    // ENVIRONMENT / BACKEND URL CONFIGURATION
    // =========================================================================

    /**
     * Deployment environments for backend URL selection.
     */
    enum class BackendEnvironment {
        /** Android emulator — routes 10.0.2.2 to host machine's localhost */
        EMULATOR,
        /** Physical device on the same Wi-Fi as the development machine */
        LOCAL_DEVICE,
        /** Firebase Cloud Functions (production / staging) */
        CLOUD,
    }

    /**
     * Active backend environment.
     *
     * Switch to [BackendEnvironment.EMULATOR] when running on AVD,
     * [BackendEnvironment.LOCAL_DEVICE] when testing on a physical device
     * (update [LOCAL_DEVICE_IP] to match your machine's LAN address), or
     * [BackendEnvironment.CLOUD] for production.
     */
    val ACTIVE_BACKEND_ENV: BackendEnvironment = BackendEnvironment.EMULATOR

    /** Replace with the LAN IP of your development machine when using a physical device. */
    const val LOCAL_DEVICE_IP = "192.168.1.100"
    const val LOCAL_BACKEND_PORT = 8000

    // =========================================================================
    // API ENDPOINTS
    // =========================================================================

    /** FastAPI backend URL when running on an Android emulator. */
    const val EMULATOR_API_BASE_URL = "http://10.0.2.2:$LOCAL_BACKEND_PORT/"

    /** FastAPI backend URL when running on a physical device on the local network. */
    val LOCAL_DEVICE_API_BASE_URL = "http://$LOCAL_DEVICE_IP:$LOCAL_BACKEND_PORT/"

    /** Firebase Cloud Functions backend URL (production). */
    const val FIREBASE_API_BASE_URL = "https://us-central1-medical-processor.cloudfunctions.net/"

    /** Deprecated alias kept for backwards-compatibility. */
    const val LOCAL_API_BASE_URL = EMULATOR_API_BASE_URL

    /**
     * Returns the appropriate base URL for the currently active backend environment.
     */
    fun resolveBaseUrl(): String = when (ACTIVE_BACKEND_ENV) {
        BackendEnvironment.EMULATOR -> EMULATOR_API_BASE_URL
        BackendEnvironment.LOCAL_DEVICE -> LOCAL_DEVICE_API_BASE_URL
        BackendEnvironment.CLOUD -> FIREBASE_API_BASE_URL
    }

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
        "dcm",
        "dicom",
        "png",
        "jpg",
        "jpeg",
    )

    val SUPPORTED_MIME_TYPES = listOf(
        "application/octet-stream",
        "application/gzip",
        "application/x-gzip",
        "image/nii",
        "application/dicom",
        "image/png",
        "image/jpeg",
    )

    const val UPLOAD_CHUNK_SIZE = 5 * 1024 * 1024 // 5 MB

    // =========================================================================
    // IMAGE COMPRESSION (Firebase free-tier optimization)
    // =========================================================================

    /** JPEG quality used when compressing images before upload (0–100). */
    const val IMAGE_COMPRESSION_QUALITY = 80

    /** Maximum dimension (width or height) for compressed images in pixels. */
    const val IMAGE_MAX_DIMENSION_PX = 512

    // =========================================================================
    // PROCESSING CONFIGURATION
    // =========================================================================

    const val POLLING_INTERVAL_MS = 2000L
    const val MAX_POLLING_ATTEMPTS = 1800
    const val PROCESSING_TIMEOUT_MS = 3600000L
    const val ESTIMATED_PROCESSING_TIME_SECONDS = 120

    // =========================================================================
    // RETRY / BACKOFF CONFIGURATION
    // =========================================================================

    /** Maximum number of automatic retry attempts for failed API calls. */
    const val MAX_RETRY_ATTEMPTS = 3

    /** Initial delay before the first retry in milliseconds. */
    const val RETRY_INITIAL_DELAY_MS = 1_000L

    /** Multiplier applied to the delay between successive retry attempts. */
    const val RETRY_BACKOFF_MULTIPLIER = 2.0

    /** Maximum delay cap between retries in milliseconds. */
    const val RETRY_MAX_DELAY_MS = 30_000L

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
    const val ERROR_BACKEND_OFFLINE = "Local backend is offline. Start the FastAPI server and retry."
    const val ERROR_INVALID_FILE = "Invalid file format. Please use .nii, .nii.gz, .dcm, .png, or .jpg"
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
