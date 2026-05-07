package com.medical.fileprocessor.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.medical.fileprocessor.util.Constants
import com.medical.fileprocessor.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Storage implementation of StorageRepository
 *
 * Handles file uploads to Firebase Storage with:
 * - Progress tracking
 * - Cancellation support
 * - Error handling
 * - Proper resource cleanup
 * - Free-tier size validation
 */
@Singleton
class FirebaseStorageRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    @ApplicationContext private val context: Context
) : StorageRepository {

    override fun uploadFile(uri: Uri, fileName: String): Flow<Resource<String>> = callbackFlow {
        try {
            Timber.tag("STORAGE").d("📤 Starting upload: $fileName")
            trySend(Resource.Loading())

            // Convert URI to file
            val file = getFileFromUri(uri)
                ?: throw IllegalStateException("Could not open file from URI: $uri")

            // Enforce free-tier size limit
            val maxBytes = Constants.MAX_FILE_SIZE_MB * 1024 * 1024
            if (file.length() > maxBytes) {
                file.delete()
                throw IllegalStateException(Constants.ERROR_FILE_TOO_LARGE)
            }

            // Create storage reference path: uploads/{fileName}
            val storageRef = firebaseStorage.reference.child("uploads/$fileName")

            // Create upload task
            val uploadTask = storageRef.putFile(Uri.fromFile(file))

            // Listen to upload progress
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toLong()
                Timber.tag("STORAGE").d("📤 Upload progress: $progress%")
                trySend(Resource.Loading(progress.toString()))
            }

            // Handle upload completion
            uploadTask.addOnSuccessListener {
                Timber.tag("STORAGE").i("✅ File uploaded successfully: $fileName")

                // Get download URL
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val url = downloadUri.toString()
                    Timber.tag("STORAGE").d("📥 Download URL: $url")
                    trySend(Resource.Success(url))
                    close()
                }
                    .addOnFailureListener { exception ->
                        Timber.tag("STORAGE").e(exception, "❌ Failed to get download URL")
                        trySend(Resource.Error(exception))
                        close()
                    }
            }

            // Handle upload failure
            uploadTask.addOnFailureListener { exception ->
                Timber.tag("STORAGE").e(exception, "❌ File upload failed: $fileName")
                trySend(Resource.Error(exception))
                close()
            }

            // Handle cancellation
            awaitClose {
                uploadTask.cancel()
                file.delete()
                Timber.tag("STORAGE").d("🧹 Upload cleanup completed")
            }

        } catch (e: Exception) {
            Timber.tag("STORAGE").e(e, "❌ Upload error: ${e.localizedMessage}")
            trySend(Resource.Error(e))
            close()
        }
    }

    /**
     * Converts a content URI to a File object.
     */
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Timber.tag("STORAGE").w("⚠️ Could not open input stream from URI: $uri")
                return null
            }

            val fileName = extractFileName(uri)
            val tempFile = File(context.cacheDir, fileName)

            FileOutputStream(tempFile).use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }

            Timber.tag("STORAGE").d("✅ File converted from URI: ${tempFile.absolutePath}")
            tempFile

        } catch (e: Exception) {
            Timber.tag("STORAGE").e(e, "❌ Error converting URI to file: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Extracts filename from a URI.
     */
    private fun extractFileName(uri: Uri): String {
        val lastSegment = uri.lastPathSegment?.substringAfterLast("/")
        return lastSegment?.takeIf { it.isNotEmpty() }
            ?: "upload_${System.currentTimeMillis()}"
    }
}

