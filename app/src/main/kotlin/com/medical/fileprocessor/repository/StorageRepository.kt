package com.medical.fileprocessor.repository

import android.net.Uri
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface for Cloud Storage operations (Firebase Storage)
 */
interface StorageRepository {
    /**
     * Uploads a file to the cloud storage
     * @param uri Local URI of the file
     * @param fileName Destination name in the cloud
     * @return Flow emitting upload progress and final URL
     */
    fun uploadFile(uri: Uri, fileName: String): Flow<Resource<String>>
}
