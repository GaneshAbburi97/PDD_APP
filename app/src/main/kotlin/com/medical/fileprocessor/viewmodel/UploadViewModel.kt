package com.medical.fileprocessor.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medical.fileprocessor.network.NetworkManager
import com.medical.fileprocessor.network.ProcessRequest
import com.medical.fileprocessor.network.ProcessResponse
import com.medical.fileprocessor.repository.AuthRepository
import com.medical.fileprocessor.repository.ProcessRepository
import com.medical.fileprocessor.util.Constants
import com.medical.fileprocessor.util.ImageCompressor
import com.medical.fileprocessor.util.ProgressRequestBody
import com.medical.fileprocessor.util.Resource
import com.medical.fileprocessor.util.getFileName
import com.medical.fileprocessor.util.getFileSize
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * UI State for the Upload Screen
 */
data class UploadUiState(
    val selectedFileUri: Uri? = null,
    val fileName: String? = null,
    val status: Resource<ProcessResponse>? = null,
    val uploadProgress: Int = 0,
    val isNetworkAvailable: Boolean = true,
    val isBackendOnline: Boolean = true
)

/**
 * ViewModel for File Upload
 * Orchestrates: Local Pick -> Direct Multipart Upload to Backend -> Backend Process
 */
@HiltViewModel
class UploadViewModel @Inject constructor(
    private val processRepository: ProcessRepository,
    private val authRepository: AuthRepository,
    private val networkManager: NetworkManager,
    private val imageCompressor: ImageCompressor,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    init {
        observeNetwork()
        checkBackendHealth()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkManager.observeNetworkStatus().collectLatest { isAvailable ->
                _uiState.value = _uiState.value.copy(isNetworkAvailable = isAvailable)
            }
        }
    }

    fun checkBackendHealth() {
        viewModelScope.launch {
            processRepository.checkBackendHealth().collectLatest { resource ->
                _uiState.value = _uiState.value.copy(isBackendOnline = resource is Resource.Success)
            }
        }
    }

    fun onFileSelected(uri: Uri, name: String) {
        _uiState.value = _uiState.value.copy(selectedFileUri = uri, fileName = name, status = null, uploadProgress = 0)
    }

    fun startUploadAndProcess() {
        val uri = _uiState.value.selectedFileUri ?: return
        val fileName = _uiState.value.fileName ?: "file_${System.currentTimeMillis()}.nii"
        val normalizedFileName = fileName.lowercase()

        val isSupportedExtension = Constants.SUPPORTED_FILE_EXTENSIONS.any {
            normalizedFileName.endsWith(".${it.lowercase()}")
        }
        val mimeType = context.contentResolver.getType(uri)
        val isSupportedMime = mimeType == null || Constants.SUPPORTED_MIME_TYPES.contains(mimeType.lowercase())
        if (!isSupportedExtension || !isSupportedMime) {
            _uiState.value = _uiState.value.copy(status = Resource.Error(Exception(Constants.ERROR_INVALID_FILE)))
            return
        }
        
        // Check size limit for research mode (500MB)
        val fileSize = uri.getFileSize(context)
        if (fileSize > Constants.MAX_FILE_SIZE_MB * 1024 * 1024) {
            _uiState.value = _uiState.value.copy(status = Resource.Error(Exception(Constants.ERROR_FILE_TOO_LARGE)))
            return
        }

        val userEmail = authRepository.getCurrentUser()?.email ?: "guest@medical.com"

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(status = Resource.Loading(), uploadProgress = 0)

                // 1. Image Compression (if applicable)
                var finalFile: File? = null
                if (fileName.endsWith(".jpg", ignoreCase = true) || fileName.endsWith(".png", ignoreCase = true)) {
                    imageCompressor.compressImage(uri)?.let { compressedFile ->
                        finalFile = compressedFile
                        Timber.tag("UPLOAD_VM").d("Image compressed: ${compressedFile.length()} bytes")
                    }
                }

                // If not compressed, copy Uri to local temp file in cache
                if (finalFile == null) {
                    finalFile = getFileFromUri(uri, fileName)
                }

                if (finalFile == null || !finalFile.exists()) {
                    throw IllegalStateException("Failed to retrieve local file copy from URI")
                }

                // 2. Direct Multipart Upload to Backend using ProgressRequestBody
                val uploadMimeType = mimeType ?: "application/octet-stream"
                val progressRequestBody = ProgressRequestBody(finalFile, uploadMimeType) { progress ->
                    _uiState.value = _uiState.value.copy(uploadProgress = progress)
                }
                val filePart = MultipartBody.Part.createFormData("file", finalFile.name, progressRequestBody)

                processRepository.uploadFile(filePart).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(status = Resource.Loading())
                        }
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(uploadProgress = 100)
                            Timber.tag("UPLOAD_VM").i("✅ Multipart upload success: ${resource.data.fileUrl}")
                            
                            // Clean up temp file
                            try {
                                finalFile.delete()
                            } catch (e: Exception) {
                                Timber.tag("UPLOAD_VM").w("Failed to delete temp file: ${e.message}")
                            }

                            // 3. Start Processing on Backend
                            val request = ProcessRequest(
                                fileUrl = resource.data.fileUrl,
                                fileName = resource.data.fileName,
                                fileSize = fileSize,
                                cloudProvider = Constants.DEFAULT_CLOUD_PROVIDER.name.lowercase(),
                                userEmail = userEmail
                            )
                            startBackendProcessing(request)
                        }
                        is Resource.Error -> {
                            Timber.tag("UPLOAD_VM").e(resource.exception, "Upload failed: ${resource.message}")
                            _uiState.value = _uiState.value.copy(status = Resource.Error(resource.exception))
                            
                            // Clean up temp file
                            try {
                                finalFile.delete()
                            } catch (e: Exception) {
                                Timber.tag("UPLOAD_VM").w("Failed to delete temp file: ${e.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.tag("UPLOAD_VM").e(e, "Upload error: ${e.message}")
                _uiState.value = _uiState.value.copy(status = Resource.Error(e))
            }
        }
    }

    private suspend fun startBackendProcessing(request: ProcessRequest) {
        try {
            processRepository.startProcessing(request).collect { result ->
                _uiState.value = _uiState.value.copy(status = result)
            }
        } catch (e: Exception) {
            Timber.tag("UPLOAD_VM").e(e, "Backend processing error: ${e.message}")
            _uiState.value = _uiState.value.copy(status = Resource.Error(e))
        }
    }

    private fun getFileFromUri(uri: Uri, name: String): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, name)
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            Timber.tag("UPLOAD_VM").e(e, "Error converting URI to file: ${e.localizedMessage}")
            null
        }
    }

    fun resetState() {
        _uiState.value = UploadUiState()
        Timber.tag("UPLOAD_VM").d("State reset")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.tag("UPLOAD_VM").d("ViewModel cleared - all upload tasks cancelled")
    }
}
