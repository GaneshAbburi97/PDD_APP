package com.medical.fileprocessor.model

import java.io.Serializable

/**
 * Data class representing the result of a medical file processing job.
 */
data class ProcessingResult(
    val resultId: String,
    val outputUrl: String,
    val thumbnailUrl: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis(),
) : Serializable
