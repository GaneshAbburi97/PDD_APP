package com.medical.fileprocessor.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingStatus
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore implementation for job tracking and metadata persistence.
 * 
 * Firestore Collection Structure:
 * jobs/
 *   {jobId}/
 *     - userId: String (who owns this job)
 *     - fileName: String (uploaded filename)
 *     - status: String (UPLOADING, QUEUED, PROCESSING, COMPLETED, FAILED)
 *     - inputFileUrl: String (Firebase Storage URL)
 *     - outputFileUrl: String (Firebase Storage URL) - populated on completion
 *     - errorMessage: String (if status is FAILED)
 *     - createdAt: Long (timestamp)
 *     - updatedAt: Long (timestamp)
 *     - progress: Int (0-100)
 * 
 * Benefits of Firestore:
 * - Realtime updates via listeners
 * - Scales automatically
 * - Built-in security rules
 * - Offline support
 */
@Singleton
class FirestoreJobRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val JOBS_COLLECTION = "jobs"
    }

    /**
     * Creates a new job record in Firestore.
     * 
     * Called when user uploads a file.
     * 
     * @param jobId Unique job identifier
     * @param userId Owner of the job (from Firebase Auth)
     * @param fileName Name of uploaded file
     * @param fileUrl Firebase Storage URL of uploaded file
     */
    suspend fun createJob(
        jobId: String,
        userId: String,
        fileName: String,
        fileUrl: String
    ) {
        try {
            Timber.tag("FIRESTORE").d("📝 Creating job: $jobId for user: $userId")

            val jobData = mapOf(
                "jobId" to jobId,
                "userId" to userId,
                "fileName" to fileName,
                "inputFileUrl" to fileUrl,
                "status" to ProcessingStatus.UPLOADING.name,
                "progress" to 0,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis(),
                "errorMessage" to ""
            )

            firestore.collection(JOBS_COLLECTION)
                .document(jobId)
                .set(jobData)
                .await()

            Timber.tag("FIRESTORE").i("✅ Job created: $jobId")

        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to create job: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Updates job status in Firestore.
     * 
     * Called by backend when job status changes.
     * Used in combination with backend API.
     * 
     * @param jobId Job to update
     * @param status New status
     * @param progress Progress percentage (0-100)
     * @param errorMessage Error message if status is FAILED
     */
    suspend fun updateJobStatus(
        jobId: String,
        status: ProcessingStatus,
        progress: Int = 0,
        errorMessage: String = ""
    ) {
        try {
            Timber.tag("FIRESTORE").d("🔄 Updating job status: $jobId -> $status")

            val updates = mutableMapOf<String, Any>(
                "status" to status.name,
                "progress" to progress,
                "updatedAt" to System.currentTimeMillis()
            )

            if (errorMessage.isNotEmpty()) {
                updates["errorMessage"] = errorMessage
            }

            firestore.collection(JOBS_COLLECTION)
                .document(jobId)
                .update(updates)
                .await()

            Timber.tag("FIRESTORE").i("✅ Job updated: $jobId -> $status")

        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to update job: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Sets result URL when processing completes.
     * 
     * Called when backend finishes processing and uploads output file.
     * 
     * @param jobId Job that completed
     * @param outputFileUrl Firebase Storage URL of result file
     */
    suspend fun setJobResult(
        jobId: String,
        outputFileUrl: String
    ) {
        try {
            Timber.tag("FIRESTORE").d("📤 Setting job result: $jobId")

            firestore.collection(JOBS_COLLECTION)
                .document(jobId)
                .update(mapOf(
                    "outputFileUrl" to outputFileUrl,
                    "status" to ProcessingStatus.COMPLETED.name,
                    "updatedAt" to System.currentTimeMillis(),
                    "progress" to 100
                ))
                .await()

            Timber.tag("FIRESTORE").i("✅ Job result set: $jobId")

        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to set job result: ${e.localizedMessage}")
            throw e
        }
    }

    /**
     * Listens to real-time job status updates.
     * 
     * Returns a Flow that emits job updates whenever Firestore document changes.
     * Used in UI to show live progress without polling.
     * 
     * Flow emits:
     * - Loading state initially
     * - Success with ProcessingJob data
     * - Error if listener fails
     * 
     * @param jobId Job to listen to
     * @return Flow of Resource<ProcessingJob>
     */
    fun listenToJob(jobId: String): Flow<Resource<ProcessingJob>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            Timber.tag("FIRESTORE").d("👂 Listening to job: $jobId")

            val listener = firestore.collection(JOBS_COLLECTION)
                .document(jobId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Timber.tag("FIRESTORE").e(error, "❌ Listener error for job: $jobId")
                        trySend(Resource.Error(error))
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        try {
                            val data = snapshot.data ?: return@addSnapshotListener
                            
                            val job = ProcessingJob(
                                jobId = data["jobId"] as? String ?: jobId,
                                status = ProcessingStatus.valueOf(
                                    (data["status"] as? String) ?: ProcessingStatus.PENDING.name
                                ),
                                progress = (data["progress"] as? Long)?.toInt() ?: 0,
                                fileName = data["fileName"] as? String,
                                resultUrl = data["outputFileUrl"] as? String,
                                error = data["errorMessage"] as? String
                            )

                            Timber.tag("FIRESTORE").d("📊 Job update received: $jobId - ${job.status}")
                            trySend(Resource.Success(job))

                        } catch (e: Exception) {
                            Timber.tag("FIRESTORE").e(e, "❌ Error parsing job snapshot")
                            trySend(Resource.Error(e))
                        }
                    } else {
                        Timber.tag("FIRESTORE").w("⚠️ Job document not found: $jobId")
                        trySend(Resource.Error(Exception("Job not found")))
                    }
                }

            // Cleanup when Flow is cancelled
            awaitClose {
                listener.remove()
                Timber.tag("FIRESTORE").d("🧹 Stopped listening to job: $jobId")
            }

        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to listen to job: ${e.localizedMessage}")
            trySend(Resource.Error(e))
            close()
        }
    }

    /**
     * Retrieves a single job snapshot (one-time read).
     * 
     * Used for initial load or explicit refresh.
     * Does NOT listen for updates.
     * 
     * @param jobId Job to retrieve
     * @return Resource<ProcessingJob>
     */
    suspend fun getJob(jobId: String): Resource<ProcessingJob> {
        return try {
            Timber.tag("FIRESTORE").d("📖 Fetching job: $jobId")

            val snapshot = firestore.collection(JOBS_COLLECTION)
                .document(jobId)
                .get()
                .await()

            if (snapshot.exists()) {
                val data = snapshot.data ?: return Resource.Error(Exception("Job data is null"))

                val job = ProcessingJob(
                    jobId = data["jobId"] as? String ?: jobId,
                    status = ProcessingStatus.valueOf(
                        (data["status"] as? String) ?: ProcessingStatus.PENDING.name
                    ),
                    progress = (data["progress"] as? Long)?.toInt() ?: 0,
                    fileName = data["fileName"] as? String,
                    resultUrl = data["outputFileUrl"] as? String,
                    error = data["errorMessage"] as? String
                )

                Timber.tag("FIRESTORE").i("✅ Job fetched: $jobId - ${job.status}")
                Resource.Success(job)

            } else {
                Timber.tag("FIRESTORE").w("⚠️ Job not found: $jobId")
                Resource.Error(Exception("Job not found"))
            }

        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to get job: ${e.localizedMessage}")
            Resource.Error(e)
        }
    }

    /**
     * Retrieves all jobs for current user.
     * 
     * Used for job history/list screen.
     * 
     * @param userId Owner of jobs
     * @return Resource<List<ProcessingJob>>
     */
    suspend fun getUserJobs(userId: String): Resource<List<ProcessingJob>> {
        return try {
            Timber.tag("FIRESTORE").d("📋 Fetching jobs for user: $userId")

            val snapshot = firestore.collection(JOBS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100) // Limit to last 100 jobs
                .get()
                .await()

            val jobs = snapshot.documents.mapNotNull { doc ->
                try {
                    val data = doc.data ?: return@mapNotNull null

                    ProcessingJob(
                        jobId = data["jobId"] as? String ?: "",
                        status = ProcessingStatus.valueOf(
                            (data["status"] as? String) ?: ProcessingStatus.PENDING.name
                        ),
                        progress = (data["progress"] as? Long)?.toInt() ?: 0,
                        fileName = data["fileName"] as? String,
                        resultUrl = data["outputFileUrl"] as? String,
                        error = data["errorMessage"] as? String
                    )
                } catch (e: Exception) {
                    Timber.tag("FIRESTORE").w("⚠️ Error parsing job doc: ${e.localizedMessage}")
                    null
                }
            }

            Timber.tag("FIRESTORE").i("✅ Fetched ${jobs.size} jobs for user: $userId")
            Resource.Success(jobs)

        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to get user jobs: ${e.localizedMessage}")
            Resource.Error(e)
        }
    }
}
