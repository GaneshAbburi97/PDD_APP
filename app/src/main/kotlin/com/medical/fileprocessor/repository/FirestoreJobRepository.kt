package com.medical.fileprocessor.repository

import com.google.firebase.firestore.FirebaseFirestore
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
 * Firestore implementation for job tracking and real-time updates.
 *
 * FIRESTORE COST OPTIMIZATION:
 * - Listeners only added when needed (not on every state change)
 * - Real-time listeners instead of polling (cheaper than repeated queries)
 * - Batch writes for multiple updates (not implemented here but pattern available)
 * - Document reads cached by Firestore SDK
 *
 * LISTENER SAFETY:
 * - Listeners automatically removed when Flow stops
 * - No orphan listeners in memory
 * - Lifecycle-safe when used with viewModelScope
 */
@Singleton
class FirestoreJobRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    companion object {
        const val COLLECTION_JOBS = "jobs"
    }

    /**
     * Create a new job record in Firestore.
     *
     * @param jobId Unique job identifier
     * @param userId Firebase user ID
     * @param fileName Name of the uploaded file
     * @param fileUrl Cloud Storage URL of the file
     */
    suspend fun createJob(
        jobId: String,
        userId: String,
        fileName: String,
        fileUrl: String
    ) {
        try {
            val job = mapOf(
                "jobId" to jobId,
                "userId" to userId,
                "fileName" to fileName,
                "inputFileUrl" to fileUrl,
                "status" to ProcessingStatus.UPLOADING.name,
                "progress" to 0,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection(COLLECTION_JOBS)
                .document(jobId)
                .set(job)
                .await()

            Timber.tag("FIRESTORE").i("✅ Job created: $jobId")
        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to create job: $jobId")
            throw e
        }
    }

    /**
     * Get a single job record (one-time read).
     *
     * @param jobId Job to retrieve
     * @return Job data or null if not found
     */
    suspend fun getJob(jobId: String): ProcessingJob? {
        return try {
            val snapshot = firestore.collection(COLLECTION_JOBS)
                .document(jobId)
                .get()
                .await()

            mapDocumentToJob(snapshot.data)
        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to get job: $jobId")
            null
        }
    }

    /**
     * Update job status and progress.
     *
     * FIRESTORE COST OPTIMIZATION:
     * - Uses update() instead of set() to only update changed fields
     * - Sends only necessary data (not full document)
     *
     * @param jobId Job to update
     * @param status New status
     * @param progress Progress 0-100
     */
    suspend fun updateJobStatus(
        jobId: String,
        status: String,
        progress: Int
    ) {
        try {
            firestore.collection(COLLECTION_JOBS)
                .document(jobId)
                .update(mapOf(
                    "status" to status,
                    "progress" to progress,
                    "updatedAt" to System.currentTimeMillis()
                ))
                .await()

            Timber.tag("FIRESTORE").d("📝 Job updated: $jobId -> $status ($progress%)")
        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to update job status: $jobId")
            throw e
        }
    }

    /**
     * Set job result when processing completes.
     *
     * @param jobId Job that completed
     * @param resultUrl URL of result file in Cloud Storage
     * @param metadata Processing metadata (volume, scores, etc)
     */
    suspend fun setJobResult(
        jobId: String,
        resultUrl: String,
        metadata: Map<String, Any>? = null
    ) {
        try {
            val updates = mutableMapOf<String, Any>(
                "outputFileUrl" to resultUrl,
                "status" to ProcessingStatus.COMPLETED.name,
                "progress" to 100,
                "updatedAt" to System.currentTimeMillis()
            )

            if (metadata != null) {
                updates["metadata"] = metadata
            }

            firestore.collection(COLLECTION_JOBS)
                .document(jobId)
                .update(updates)
                .await()

            Timber.tag("FIRESTORE").i("✅ Job result set: $jobId")
        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to set job result: $jobId")
            throw e
        }
    }

    /**
     * Listen to real-time updates for a single job.
     *
     * LISTENER SAFETY:
     * - Listener automatically removed via awaitClose when Flow stops
     * - No memory leaks (Firestore tracks listeners internally)
     * - Safe for viewModelScope (cancelled when ViewModel cleared)
     *
     * REAL-TIME BENEFITS:
     * - Push updates instead of polling
     * - Immediate status changes
     * - Lower latency than polling
     * - Fewer network requests than polling
     *
     * @param jobId Job to listen to
     * @return Flow emitting job updates whenever document changes
     */
    fun listenToJob(jobId: String): Flow<Resource<ProcessingJob>> = callbackFlow {
        try {
            Timber.tag("FIRESTORE").d("👂 Registering listener for job: $jobId")

            val listener = firestore.collection(COLLECTION_JOBS)
                .document(jobId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Timber.tag("FIRESTORE").e(exception, "❌ Listener error for $jobId")
                        trySend(Resource.Error(exception))
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val job = mapDocumentToJob(snapshot.data)
                        if (job != null) {
                            Timber.tag("FIRESTORE").d("📊 Job update: $jobId - ${job.status}")
                            trySend(Resource.Success(job))
                        }
                    } else {
                        Timber.tag("FIRESTORE").w("⚠️ Job not found: $jobId")
                        trySend(Resource.Error(Exception("Job not found")))
                    }
                }

            awaitClose {
                listener.remove()
                Timber.tag("FIRESTORE").d("🧹 Listener removed for job: $jobId")
            }
        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to setup listener for $jobId")
            trySend(Resource.Error(e))
            close()
        }
    }

    /**
     * Get all jobs for a user.
     *
     * @param userId Firebase user ID
     * @return List of user's jobs
     */
    suspend fun getUserJobs(userId: String): List<ProcessingJob> {
        return try {
            val snapshots = firestore.collection(COLLECTION_JOBS)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt")
                .get()
                .await()

            snapshots.documents.mapNotNull { mapDocumentToJob(it.data) }
        } catch (e: Exception) {
            Timber.tag("FIRESTORE").e(e, "❌ Failed to get user jobs: $userId")
            emptyList()
        }
    }

    /**
     * Map Firestore document to ProcessingJob model.
     *
     * @param data Raw Firestore document data
     * @return ProcessingJob or null if data invalid
     */
    private fun mapDocumentToJob(data: Map<String, Any>?): ProcessingJob? {
        if (data == null) return null

        return try {
            ProcessingJob(
                jobId = data["jobId"] as? String ?: return null,
                fileName = data["fileName"] as? String ?: "unknown",
                status = ProcessingStatus.valueOf(
                    (data["status"] as? String ?: "QUEUED").uppercase()
                ),
                progress = (data["progress"] as? Number)?.toInt() ?: 0,
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: 0L,
                resultUrl = data["outputFileUrl"] as? String,
                metadata = (data["metadata"] as? Map<*, *>)?.entries?.associate { 
                    it.key.toString() to it.value.toString() 
                }
            )
        } catch (e: Exception) {
            Timber.tag("FIRESTORE").w(e, "⚠️ Failed to map job document: ${e.message}")
            null
        }
    }
}

