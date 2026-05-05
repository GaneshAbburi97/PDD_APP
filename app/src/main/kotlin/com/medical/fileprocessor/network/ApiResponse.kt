package com.medical.fileprocessor.network

/**
 * Generic API response wrapper.
 * 
 * Standard structure for all backend responses to ensure 
 * consistent handling of success, data, and error messages.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val code: Int = if (success) 200 else 400,
    val timestamp: Long = System.currentTimeMillis()
)
