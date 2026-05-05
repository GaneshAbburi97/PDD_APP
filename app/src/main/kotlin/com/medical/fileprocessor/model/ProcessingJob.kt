package com.medical.fileprocessor.model

import java.io.Serializable

/**
 * Data class representing a medical file processing job.
 */
data class ProcessingJob(
    val jobId: String,
    val status: ProcessingStatus,
    val progress: Int,
    val fileName: String? = null,
    val resultUrl: String? = null,
    val error: String? = null,
) : Serializable

/**
 * Enum representing the possible states of a processing job.
 */
enum class ProcessingStatus(val displayName: String) {
    PENDING("Pending"),
    UPLOADING("Uploading"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
}
