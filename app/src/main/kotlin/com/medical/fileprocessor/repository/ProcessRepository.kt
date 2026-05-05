package com.medical.fileprocessor.repository

import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingResult
import com.medical.fileprocessor.network.ProcessRequest
import com.medical.fileprocessor.network.ProcessResponse
import com.medical.fileprocessor.network.UploadResponse
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

/**
 * Interface defining operations for medical file processing.
 */
interface ProcessRepository {
    
    /**
     * Uploads a file directly to the processing backend.
     */
    fun uploadFile(file: MultipartBody.Part): Flow<Resource<UploadResponse>>

    /**
     * Starts the processing of a medical file already in cloud storage.
     */
    fun startProcessing(request: ProcessRequest): Flow<Resource<ProcessResponse>>

    /**
     * Polls the current status of a processing job.
     */
    fun getJobStatus(jobId: String): Flow<Resource<ProcessingJob>>

    /**
     * Fetches the detailed results once a job is COMPLETED.
     */
    fun getProcessingResult(jobId: String): Flow<Resource<ProcessingResult>>
}
