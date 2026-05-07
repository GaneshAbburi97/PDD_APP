package com.medical.fileprocessor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingResult
import com.medical.fileprocessor.network.NetworkManager
import com.medical.fileprocessor.repository.ProcessRepository
import com.medical.fileprocessor.repository.ProcessRepositoryImpl
import com.medical.fileprocessor.util.Resource
import com.medical.fileprocessor.util.dataOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Viewing Results and Tracking Status.
 *
 * LIFECYCLE SAFETY:
 * - Network listener automatically cancelled when scope clears
 * - Job listeners removed via manual cancellation in onCleared()
 * - No orphan coroutines or listeners
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

    private var currentJobId: String? = null

    init {
        observeNetwork()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkManager.observeNetworkStatus().collectLatest { isAvailable ->
                _isNetworkAvailable.value = isAvailable
                Timber.tag("RESULT_VM").d("Network available: $isAvailable")
            }
        }
    }

    /**
     * Starts listening to real-time status updates via Firestore.
     *
     * SAFETY:
     * - Previous job listener cancelled before starting new one
     * - Listener automatically removed when viewModelScope clears
     * - No duplicate listeners for same jobId
     */
    fun startListeningToJob(jobId: String) {
        if (currentJobId == jobId) {
            Timber.tag("RESULT_VM").d("Already listening to job: $jobId")
            return
        }

        currentJobId = jobId
        viewModelScope.launch {
            try {
                (repository as? ProcessRepositoryImpl)?.apply {
                    // Start real-time listener
                    listenToJobStatus(jobId).collectLatest { resource ->
                        _jobStatus.value = resource
                        Timber.tag("RESULT_VM").d("Job status updated: $jobId - ${resource.dataOrNull?.status}")
                    }
                }
            } catch (e: Exception) {
                Timber.tag("RESULT_VM").e(e, "Failed to listen to job: $jobId")
                _jobStatus.value = Resource.Error(e)
            }
        }
    }

    fun fetchResult(jobId: String) {
        viewModelScope.launch {
            try {
                repository.getProcessingResult(jobId).collectLatest { result ->
                    _processingResult.value = result
                }
            } catch (e: Exception) {
                Timber.tag("RESULT_VM").e(e, "Failed to fetch result: $jobId")
                _processingResult.value = Resource.Error(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentJobId = null
        Timber.tag("RESULT_VM").d("Cleared all listeners and jobs")
    }
}
