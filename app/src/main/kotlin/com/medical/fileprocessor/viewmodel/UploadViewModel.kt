package com.medical.fileprocessor.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medical.fileprocessor.network.ProcessRequest
import com.medical.fileprocessor.network.ProcessResponse
import com.medical.fileprocessor.repository.AuthRepository
import com.medical.fileprocessor.repository.ProcessRepository
import com.medical.fileprocessor.repository.StorageRepository
import com.medical.fileprocessor.util.Constants
import com.medical.fileprocessor.util.Resource
import com.medical.fileprocessor.util.getFileSize
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for the Upload Screen
 */
data class UploadUiState(
    val selectedFileUri: Uri? = null,
    val fileName: String? = null,
    val status: Resource<ProcessResponse>? = null,
)

/**
 * ViewModel for File Upload
 * Orchestrates: Local Pick -> Cloud Upload -> Backend Process
 */
@HiltViewModel
class UploadViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val processRepository: ProcessRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri, name: String) {
        _uiState.value = _uiState.value.copy(selectedFileUri = uri, fileName = name, status = null)
    }

    fun startUploadAndProcess() {
        val uri = _uiState.value.selectedFileUri ?: return
        val fileName = _uiState.value.fileName ?: "file_${System.currentTimeMillis()}.nii"
        val userEmail = authRepository.getCurrentUser()?.email ?: "guest@medical.com"

        viewModelScope.launch {
            // 1. Upload to Storage
            storageRepository.uploadFile(uri, fileName).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(status = Resource.Loading())
                    }
                    is Resource.Success -> {
                        // 2. Start Processing on Backend
                        val fileUrl = resource.data
                        val fileSize = uri.getFileSize(context)
                        val request = ProcessRequest(
                            fileUrl = fileUrl,
                            fileName = fileName,
                            fileSize = fileSize,
                            cloudProvider = Constants.DEFAULT_CLOUD_PROVIDER.name.lowercase(),
                            userEmail = userEmail
                        )
                        startBackendProcessing(request)
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(status = Resource.Error(resource.exception))
                    }
                }
            }
        }
    }

    private suspend fun startBackendProcessing(request: ProcessRequest) {
        processRepository.startProcessing(request).collect { result ->
            _uiState.value = _uiState.value.copy(status = result)
        }
    }

    fun resetState() {
        _uiState.value = UploadUiState()
    }
}
