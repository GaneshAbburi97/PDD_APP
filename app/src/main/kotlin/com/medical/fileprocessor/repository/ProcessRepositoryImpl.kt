package com.medical.fileprocessor.repository

import com.google.firebase.auth.FirebaseAuth
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingResult
import com.medical.fileprocessor.network.*
import com.medical.fileprocessor.util.Constants
import com.medical.fileprocessor.util.NetworkManager
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/**
 * Retrofit implementation of [ProcessRepository].
 *
 * Handles communication with the FastAPI backend for file uploading,
 * processing initialization, and status tracking.
 *
 * Also integrates with Firestore for metadata persistence and realtime updates.
 *
 * Retry behaviour:
 * - Up to [Constants.MAX_RETRY_ATTEMPTS] retries on network / server errors
 * - Exponential backoff starting at [Constants.RETRY_INITIAL_DELAY_MS]
 * - Does NOT retry on 4xx client errors (bad request, unauthorised, etc.)
 */
@Singleton
class ProcessRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreJobRepository: FirestoreJobRepository,
    private val networkManager: NetworkManager,
) : ProcessRepository {

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    override fun uploadFile(file: MultipartBody.Part): Flow<Resource<UploadResponse>> = flow {
        emit(Resource.Loading())
        if (!networkManager.isConnected) {
            emit(Resource.Error(Exception(Constants.ERROR_NO_INTERNET)))
            return@flow
        }

        val result = retryWithBackoff(tag = "uploadFile") {
            apiService.uploadFile(file)
        }
        result.fold(
            onSuccess = { response ->
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Timber.tag("PROCESS").i("✅ File uploaded: ${it.fileUrl}")
                        emit(Resource.Success(it))
                    } ?: emit(Resource.Error(Exception("Upload data is null")))
                } else {
                    emit(Resource.Error(Exception(response.body()?.message ?: "File upload failed")))
                }
            },
            onFailure = { emit(Resource.Error(it as Exception)) },
        )
    }

    override fun startProcessing(request: ProcessRequest): Flow<Resource<ProcessResponse>> = flow {
        emit(Resource.Loading())
        if (!networkManager.isConnected) {
            emit(Resource.Error(Exception(Constants.ERROR_NO_INTERNET)))
            return@flow
        }

        val result = retryWithBackoff(tag = "startProcessing") {
            apiService.startProcessing(request)
        }
        result.fold(
            onSuccess = { response ->
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { processResponse ->
                        val jobId = processResponse.jobId
                        createFirestoreJob(jobId, request)
                        Timber.tag("PROCESS").i("✅ Processing started: $jobId")
                        emit(Resource.Success(processResponse))
                    } ?: emit(Resource.Error(Exception("Response data is null")))
                } else {
                    emit(Resource.Error(Exception(response.body()?.message ?: "Processing start failed")))
                }
            },
            onFailure = { emit(Resource.Error(it as Exception)) },
        )
    }

    override fun getJobStatus(jobId: String): Flow<Resource<ProcessingJob>> = flow {
        emit(Resource.Loading())

        val result = retryWithBackoff(tag = "getJobStatus") {
            apiService.getJobStatus(jobId)
        }
        result.fold(
            onSuccess = { response ->
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Timber.tag("PROCESS").d("📊 Job status: $jobId - ${it.status}")
                        emit(Resource.Success(it))
                    } ?: emit(Resource.Error(Exception("Job data is null")))
                } else {
                    emit(Resource.Error(Exception(response.body()?.message ?: "Status check failed")))
                }
            },
            onFailure = { emit(Resource.Error(it as Exception)) },
        )
    }

    override fun getProcessingResult(jobId: String): Flow<Resource<ProcessingResult>> = flow {
        emit(Resource.Loading())

        val result = retryWithBackoff(tag = "getProcessingResult") {
            apiService.getProcessingResult(jobId)
        }
        result.fold(
            onSuccess = { response ->
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        Timber.tag("PROCESS").i("✅ Result fetched: $jobId")
                        emit(Resource.Success(it))
                    } ?: emit(Resource.Error(Exception("Result data is null")))
                } else {
                    emit(Resource.Error(Exception(response.body()?.message ?: "Result fetch failed")))
                }
            },
            onFailure = { emit(Resource.Error(it as Exception)) },
        )
    }

    /**
     * Listens to realtime job status updates via Firestore.
     *
     * Replaces polling with a realtime listener.
     * Returns a Flow that emits updates whenever job status changes.
     *
     * @param jobId Job to listen to
     * @return Flow emitting realtime job updates
     */
    fun listenToJobStatus(jobId: String): Flow<Resource<ProcessingJob>> =
        firestoreJobRepository.listenToJob(jobId)

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Executes [block] and retries up to [Constants.MAX_RETRY_ATTEMPTS] times
     * with exponential backoff on transient failures.
     *
     * 4xx HTTP responses are NOT retried – they indicate a client error that
     * won't resolve by retrying.
     */
    private suspend fun <T> retryWithBackoff(
        tag: String,
        block: suspend () -> retrofit2.Response<T>,
    ): Result<retrofit2.Response<T>> {
        var delayMs = Constants.RETRY_INITIAL_DELAY_MS
        repeat(Constants.MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                val response = block()
                // Don't retry client errors
                if (response.code() in 400..499) return Result.success(response)
                if (response.isSuccessful) return Result.success(response)
                Timber.tag("PROCESS").w("⚠️ $tag attempt ${attempt + 1} failed: HTTP ${response.code()}")
            } catch (e: Exception) {
                Timber.tag("PROCESS").w(e, "⚠️ $tag attempt ${attempt + 1} exception")
                if (attempt == Constants.MAX_RETRY_ATTEMPTS - 1) {
                    return Result.failure(e)
                }
            }
            delay(delayMs)
            delayMs = min(
                (delayMs * Constants.RETRY_BACKOFF_MULTIPLIER).toLong(),
                Constants.RETRY_MAX_DELAY_MS,
            )
        }
        // Final attempt
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createFirestoreJob(jobId: String, request: ProcessRequest) {
        try {
            val userId = firebaseAuth.currentUser?.uid ?: "unknown"
            firestoreJobRepository.createJob(
                jobId = jobId,
                userId = userId,
                fileName = request.fileName,
                fileUrl = request.fileUrl,
            )
            Timber.tag("PROCESS").i("✅ Firestore job created: $jobId")
        } catch (e: Exception) {
            // Non-fatal: Firestore write failure should not block the processing flow
            Timber.tag("PROCESS").w("⚠️ Failed to create Firestore job: ${e.localizedMessage}")
        }
    }
}
