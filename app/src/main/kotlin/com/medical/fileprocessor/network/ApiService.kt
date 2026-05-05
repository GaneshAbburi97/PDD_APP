package com.medical.fileprocessor.network

import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingResult
import com.medical.fileprocessor.model.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for medical file processing.
 *
 * Provides endpoints for authentication, file processing, status tracking,
 * and backend health monitoring.
 * All functions are suspended for use with Kotlin Coroutines.
 */
interface ApiService {

    // --- Health ---

    /**
     * Ping the local FastAPI backend.
     * Used to detect whether the backend is reachable before submitting work.
     */
    @GET("health")
    suspend fun healthCheck(): Response<HealthResponse>

    // --- Authentication ---

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<ApiResponse<User>>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest,
    ): Response<ApiResponse<User>>

    // --- File Processing ---

    /**
     * Option 1: Direct Multipart Upload to Backend
     */
    @Multipart
    @POST("upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
    ): Response<ApiResponse<UploadResponse>>

    /**
     * Option 2: Notify backend to process a file already in cloud storage
     */
    @POST("process")
    suspend fun startProcessing(
        @Body request: ProcessRequest,
    ): Response<ApiResponse<ProcessResponse>>

    // --- Results & Status ---

    /**
     * Poll for the current status of a processing job
     */
    @GET("process/status/{jobId}")
    suspend fun getJobStatus(
        @Path("jobId") jobId: String,
    ): Response<ApiResponse<ProcessingJob>>

    /**
     * Retrieve the final detailed result of a processing job
     */
    @GET("process/result/{jobId}")
    suspend fun getProcessingResult(
        @Path("jobId") jobId: String,
    ): Response<ApiResponse<ProcessingResult>>

    // --- Job Management ---

    /**
     * Cancel an in-progress job
     */
    @POST("process/cancel/{jobId}")
    suspend fun cancelJob(
        @Path("jobId") jobId: String,
    ): Response<ApiResponse<Unit>>

    /**
     * Retry a failed job
     */
    @POST("process/retry/{jobId}")
    suspend fun retryJob(
        @Path("jobId") jobId: String,
    ): Response<ApiResponse<ProcessResponse>>
}

// ─────────────────────────────────────────────────────────────────────────────
// Data Transfer Objects (DTOs) for API Requests and Responses
// ─────────────────────────────────────────────────────────────────────────────

data class HealthResponse(
    val status: String,
    val version: String? = null,
    val modelLoaded: Boolean = false,
)

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
)

data class ProcessRequest(
    val fileUrl: String,
    val fileName: String,
    val fileSize: Long,
    val cloudProvider: String,
    val userEmail: String,
)

data class ProcessResponse(
    val jobId: String,
    val estimatedTimeSeconds: Int,
    val status: String,
)

data class UploadResponse(
    val fileUrl: String,
    val fileName: String,
)
