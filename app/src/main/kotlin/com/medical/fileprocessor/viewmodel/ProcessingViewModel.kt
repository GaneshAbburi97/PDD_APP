package com.medical.fileprocessor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingStatus
import com.medical.fileprocessor.network.NetworkManager
import com.medical.fileprocessor.repository.ProcessRepository
import com.medical.fileprocessor.util.Constants
import com.medical.fileprocessor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for handling the Active Processing phase.
 * Actively polls the backend every 2 seconds for status updates.
 */
@HiltViewModel
class ProcessingViewModel @Inject constructor(
    private val repository: ProcessRepository,
    private val networkManager: NetworkManager,
) : ViewModel() {

    private val _jobStatus = MutableStateFlow<Resource<ProcessingJob>?>(null)
    val jobStatus: StateFlow<Resource<ProcessingJob>?> = _jobStatus.asStateFlow()

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

    private var currentJobId: String? = null
    private var pollingJob: Job? = null

    init {
        observeNetwork()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkManager.observeNetworkStatus().collectLatest { isAvailable ->
                _isNetworkAvailable.value = isAvailable
                Timber.tag("PROCESSING_VM").d("Network availability updated: $isAvailable")
            }
        }
    }

    /**
     * Starts polling the backend for job status updates every 2 seconds.
     * Includes duplicate prevention and lifecycle safety.
     */
    fun startListeningToJob(jobId: String) {
        if (currentJobId == jobId && pollingJob?.isActive == true) {
            Timber.tag("PROCESSING_VM").d("Already polling status for job: $jobId")
            return
        }

        // Cancel any existing polling job
        pollingJob?.cancel()
        currentJobId = jobId
        _jobStatus.value = Resource.Loading()

        pollingJob = viewModelScope.launch {
            Timber.tag("PROCESSING_VM").i("🚀 Starting active 2-second status polling for job: $jobId")
            var attempts = 0
            val maxAttempts = Constants.MAX_POLLING_ATTEMPTS

            while (attempts < maxAttempts) {
                try {
                    repository.getJobStatus(jobId).collect { resource ->
                        // Emits the current status resource
                        _jobStatus.value = resource
                        
                        when (resource) {
                            is Resource.Success -> {
                                val status = resource.data.status
                                Timber.tag("PROCESSING_VM").d("📊 Poll result: $jobId - Status: $status, Progress: ${resource.data.progress}%")
                                
                                if (status == ProcessingStatus.COMPLETED || status == ProcessingStatus.FAILED) {
                                    Timber.tag("PROCESSING_VM").i("🏁 Job reached final state: $status. Stopping polling.")
                                    pollingJob?.cancel()
                                }
                            }
                            is Resource.Error -> {
                                Timber.tag("PROCESSING_VM").w("⚠️ Poll failed: ${resource.message}")
                            }
                            is Resource.Loading -> {
                                // Keep the loading state going
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.tag("PROCESSING_VM").e(e, "❌ Error during polling loop")
                }

                attempts++
                delay(Constants.POLLING_INTERVAL_MS)
            }

            if (attempts >= maxAttempts) {
                Timber.tag("PROCESSING_VM").e("❌ Polling timed out after $maxAttempts attempts")
                _jobStatus.value = Resource.Error(Exception("Processing status check timed out"))
            }
        }
    }

    /**
     * Cancels active job processing.
     */
    fun cancelJobProcessing(jobId: String) {
        viewModelScope.launch {
            try {
                repository.cancelProcessing(jobId).collectLatest {
                    // Backend state transition is authoritative; stop local polling either way.
                }
            } catch (e: Exception) {
                Timber.tag("PROCESSING_VM").w(e, "Cancel request failed for job: $jobId")
            } finally {
                pollingJob?.cancel()
                _jobStatus.value = Resource.Error(Exception("Job processing cancelled by user"))
                Timber.tag("PROCESSING_VM").i("❌ Job cancelled by user: $jobId")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
        currentJobId = null
        Timber.tag("PROCESSING_VM").d("Cleared processing VM - polling stopped")
    }
}
