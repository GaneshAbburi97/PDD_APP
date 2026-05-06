package com.medical.fileprocessor.repository

import com.google.firebase.auth.FirebaseAuth
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingResult
import com.medical.fileprocessor.network.*
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit implementation of [ProcessRepository].
 * 
 * Handles communication with the FastAPI backend for file uploading, 
 * processing initialization, and status tracking.
 * 
 * Also integrates with Firestore for metadata persistence and realtime updates.
 */
@Singleton
class ProcessRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreJobRepository: FirestoreJobRepository
) : ProcessRepository {

    /**
     * Checks if the local/research backend is reachable and healthy.
     */
    fun checkBackendHealth(): Flow<Resource<HealthStatus>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.checkHealth()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error(Exception("Health data is null")))
            } else {
                emit(Resource.Error(Exception("Backend unreachable or unhealthy")))
            }
        } catch (e: Exception) {
            Timber.tag("PROCESS").e(e, "❌ Health check failed")
            emit(Resource.Error(e))
        }
    }

    override fun uploadFile(file: MultipartBody.Part): Flow<Resource<UploadResponse>> = flow {
        emit(Resource.Loading())
        try {
            Timber.tag("PROCESS").d("📤 Starting file upload")
            
            val response = apiService.uploadFile(file)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Timber.tag("PROCESS").i("✅ File uploaded: ${it.fileUrl}")
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error(Exception("Upload data is null")))
            } else {
                emit(Resource.Error(Exception(response.body()?.message ?: "File upload failed")))
            }
        } catch (e: Exception) {
            Timber.tag("PROCESS").e(e, "❌ Upload error: ${e.localizedMessage}")
            emit(Resource.Error(e))
        }
    }

    override fun startProcessing(request: ProcessRequest): Flow<Resource<ProcessResponse>> = flow {
        emit(Resource.Loading())
        try {
            Timber.tag("PROCESS").d("🚀 Starting processing for file: ${request.fileName}")
            
            val response = apiService.startProcessing(request)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { processResponse ->
                    val jobId = processResponse.jobId
                    
                    // Create Firestore job record
                    try {
                        val userId = firebaseAuth.currentUser?.uid ?: "unknown"
                        firestoreJobRepository.createJob(
                            jobId = jobId,
                            userId = userId,
                            fileName = request.fileName,
                            fileUrl = request.fileUrl
                        )
                        Timber.tag("PROCESS").i("✅ Firestore job created: $jobId")
                    } catch (e: Exception) {
                        Timber.tag("PROCESS").w("⚠️ Failed to create Firestore job: ${e.localizedMessage}")
                        // Don't fail the entire flow if Firestore write fails
                    }
                    
                    Timber.tag("PROCESS").i("✅ Processing started: $jobId")
                    emit(Resource.Success(processResponse))
                    
                } ?: emit(Resource.Error(Exception("Response data is null")))
            } else {
                emit(Resource.Error(Exception(response.body()?.message ?: "Processing start failed")))
            }
        } catch (e: Exception) {
            Timber.tag("PROCESS").e(e, "❌ Processing start error: ${e.localizedMessage}")
            emit(Resource.Error(e))
        }
    }

    override fun getJobStatus(jobId: String): Flow<Resource<ProcessingJob>> = flow {
        emit(Resource.Loading())
        try {
            Timber.tag("PROCESS").d("📊 Checking job status: $jobId")
            
            val response = apiService.getJobStatus(jobId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Timber.tag("PROCESS").d("📊 Job status: $jobId - ${it.status}")
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error(Exception("Job data is null")))
            } else {
                emit(Resource.Error(Exception(response.body()?.message ?: "Status check failed")))
            }
        } catch (e: Exception) {
            Timber.tag("PROCESS").e(e, "❌ Status check error: ${e.localizedMessage}")
            emit(Resource.Error(e))
        }
    }

    override fun getProcessingResult(jobId: String): Flow<Resource<ProcessingResult>> = flow {
        emit(Resource.Loading())
        try {
            Timber.tag("PROCESS").d("📥 Fetching processing result: $jobId")
            
            val response = apiService.getProcessingResult(jobId)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    Timber.tag("PROCESS").i("✅ Result fetched: $jobId")
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error(Exception("Result data is null")))
            } else {
                emit(Resource.Error(Exception(response.body()?.message ?: "Result fetch failed")))
            }
        } catch (e: Exception) {
            Timber.tag("PROCESS").e(e, "❌ Result fetch error: ${e.localizedMessage}")
            emit(Resource.Error(e))
        }
    }

    /**
     * NEW: Listens to realtime job status updates via Firestore.
     * 
     * Replaces polling with realtime listener.
     * Returns a Flow that emits updates whenever job status changes.
     * 
     * @param jobId Job to listen to
     * @return Flow emitting realtime job updates
     */
    fun listenToJobStatus(jobId: String): Flow<Resource<ProcessingJob>> {
        return firestoreJobRepository.listenToJob(jobId)
    }
}
