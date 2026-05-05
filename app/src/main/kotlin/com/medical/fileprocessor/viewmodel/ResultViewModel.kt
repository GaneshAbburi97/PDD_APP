package com.medical.fileprocessor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingResult
import com.medical.fileprocessor.repository.ProcessRepository
import com.medical.fileprocessor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Viewing Results and Tracking Status.
 */
@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: ProcessRepository,
) : ViewModel() {

    private val _jobStatus = MutableStateFlow<Resource<ProcessingJob>?>(null)
    val jobStatus: StateFlow<Resource<ProcessingJob>?> = _jobStatus.asStateFlow()

    private val _processingResult = MutableStateFlow<Resource<ProcessingResult>?>(null)
    val processingResult: StateFlow<Resource<ProcessingResult>?> = _processingResult.asStateFlow()

    fun fetchJobStatus(jobId: String) {
        viewModelScope.launch {
            repository.getJobStatus(jobId).collect {
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
