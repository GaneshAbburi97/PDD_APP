package com.medical.fileprocessor.repository

import android.net.Uri
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for cloud storage operations
 */
interface StorageRepository {

    /**
     * Uploads a file to cloud storage
     *
     * @param uri Content URI of the file to upload
     * @param fileName Name to use for the uploaded file
     * @return Flow emitting Resource<String> with download URL on success
     */
    fun uploadFile(uri: Uri, fileName: String): Flow<Resource<String>>
}

