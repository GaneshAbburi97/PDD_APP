package com.medical.fileprocessor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingResult
import com.medical.fileprocessor.network.NetworkManager
import com.medical.fileprocessor.repository.ProcessRepository
import com.medical.fileprocessor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Viewing Results and Tracking Status.
 */
@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: ProcessRepository,
    private val networkManager: NetworkManager,
) : ViewModel() {

    private val _jobStatus = MutableStateFlow<Resource<ProcessingJob>?>(null)
    val jobStatus: StateFlow<Resource<ProcessingJob>?> = _jobStatus.asStateFlow()

    private val _processingResult = MutableStateFlow<Resource<ProcessingResult>?>(null)
    val processingResult: StateFlow<Resource<ProcessingResult>?> = _processingResult.asStateFlow()

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    init {
        observeNetwork()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkManager.observeNetworkStatus().collectLatest { isAvailable ->
                _isNetworkAvailable.value = isAvailable
            }
        }
    }

    /**
     * Starts listening to real-time status updates via Firestore.
     * Replaces polling for research mode.
     */
    fun startListeningToJob(jobId: String) {
        viewModelScope.launch {
            repository.checkBackendHealth().collectLatest { _ -> } // Trigger health check log
            repository.getJobStatus(jobId).collect { // Initial poll for compatibility
                _jobStatus.value = it
            }
            // Real-time listener
            (repository as? com.medical.fileprocessor.repository.ProcessRepositoryImpl)?.listenToJobStatus(jobId)?.collect {
                _jobStatus.value = it
            }
        }
    }

    fun fetchResult(jobId: String) {
        viewModelScope.launch {
            repository.getProcessingResult(jobId).collect {
                _processingResult.value = it
            }
        }
    }
}
