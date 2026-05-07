package com.medical.fileprocessor.model

/**
 * Model representing the final result of a medical file processing job
 */
data class ProcessingResult(
    val jobId: String,
    val status: String,
    val resultUrl: String,
    val metadata: Map<String, Any>? = null,
    val processingTimeSeconds: Int? = null,
    val fileSize: Long? = null
)

