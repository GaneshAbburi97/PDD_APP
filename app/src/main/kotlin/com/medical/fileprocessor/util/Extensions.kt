package com.medical.fileprocessor.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

// ============================================================================
// FILE UTILITY EXTENSIONS
// ============================================================================

/**
 * Format bytes to human readable format
 */
fun Long.formatBytes(): String {
    if (this <= 0) return "0 B"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (ln(toDouble()) / ln(1024.0)).toInt()
    val value = this / 1024.0.pow(digitGroups.toDouble())

    return String.format(Locale.getDefault(), "%.1f %s", value, units[digitGroups])
}

// ============================================================================
// STRING UTILITY EXTENSIONS
// ============================================================================

/**
 * Validate email format using regex
 */
fun String.isValidEmail(): Boolean {
    return this.matches(
        Regex("^[A-Za-z0-9+_.-]+@(.+)\\.[A-Za-z]{2,}$"),
    )
}

/**
 * Validate password strength
 */
fun String.isStrongPassword(): Boolean {
    return (this.length >= Constants.MIN_PASSWORD_LENGTH) &&
            this.any { it.isUpperCase() } &&
            this.any { it.isLowerCase() } &&
            this.any { it.isDigit() }
}

// ============================================================================
// URI EXTENSIONS
// ============================================================================

/**
 * Get file name from URI
 */
fun Uri.getFileName(context: Context): String? {
    return when (scheme) {
        "file" -> File(path ?: return null).name
        "content" -> {
            val cursor = context.contentResolver.query(
                this,
                null,
                null,
                null,
                null,
            )
            cursor?.use {
                val nameIndex = it.getColumnIndex(
                    android.provider.OpenableColumns.DISPLAY_NAME,
                )
                it.moveToFirst()
                it.getString(nameIndex)
            }
        }
        else -> null
    }
}

/**
 * Get file size from URI in bytes
 */
fun Uri.getFileSize(context: Context): Long {
    return when (scheme) {
        "file" -> File(path ?: return 0).length()
        "content" -> {
            val cursor = context.contentResolver.query(
                this,
                null,
                null,
                null,
                null,
            )
            cursor?.use {
                val sizeIndex = it.getColumnIndex(
                    android.provider.OpenableColumns.SIZE,
                )
                it.moveToFirst()
                it.getLong(sizeIndex)
            } ?: 0
        }
        else -> 0
    }
}
