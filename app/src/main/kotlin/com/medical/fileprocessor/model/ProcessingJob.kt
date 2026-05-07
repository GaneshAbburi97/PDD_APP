package com.medical.fileprocessor.model

/**
 * Processing status values
 */
enum class ProcessingStatus {
    UPLOADING,
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED
}

/**
 * Model representing a file processing job
 *
 * LIFECYCLE:
 * - UPLOADING: File being uploaded to Cloud Storage
 * - QUEUED: In backend processing queue
 * - PROCESSING: AI inference running
 * - COMPLETED: Finished successfully with result
 * - FAILED: Error during processing
 */
data class ProcessingJob(
    val jobId: String,
    val fileName: String,
    val status: ProcessingStatus,
    val progress: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val resultUrl: String? = null,
    val metadata: Map<String, String>? = null,
    val errorMessage: String? = null
)

