package com.medical.fileprocessor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.medical.fileprocessor.model.ProcessingStatus
import com.medical.fileprocessor.ui.components.StatusCard
import com.medical.fileprocessor.util.Resource
import com.medical.fileprocessor.util.dataOrNull
import com.medical.fileprocessor.viewmodel.ResultViewModel
import kotlinx.coroutines.delay

/**
 * Screen that polls for the status of an active processing job.
 */
@Composable
fun ProcessingScreen(
    jobId: String,
    onProcessingComplete: (String) -> Unit,
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val jobStatusState by viewModel.jobStatus.collectAsState()
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    val pollingCounter = remember { mutableIntStateOf(0) }

    // Defensive check for empty jobId
    if (jobId.isBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Error: Invalid Job ID",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        return
    }

    // Start real-time listening
    LaunchedEffect(jobId) {
        viewModel.startListeningToJob(jobId)
    }

    // Observation of status for navigation
    LaunchedEffect(jobStatusState) {
        val currentStatus = jobStatusState?.dataOrNull?.status
        if (currentStatus == ProcessingStatus.COMPLETED) {
            onProcessingComplete(jobId)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (!isNetworkAvailable) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "⚠️ Offline: Check your connection",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Text(
                text = stringResource(com.medical.fileprocessor.R.string.processing_in_progress),
                style = MaterialTheme.typography.headlineSmall,
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            when (val resource = jobStatusState) {
                is Resource.Success -> {
                    StatusCard(job = resource.data)
                }
                is Resource.Loading -> {
                    CircularProgressIndicator()
                    Text(
                        text = resource.data?.status?.name ?: stringResource(com.medical.fileprocessor.R.string.connecting_server),
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                is Resource.Error -> {
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.status_check_error, resource.message ?: "Unknown error"),
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(
                        onClick = { viewModel.startListeningToJob(jobId) },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text(stringResource(com.medical.fileprocessor.R.string.retry_button))
                    }
                }
                null -> {
                    // Initial state - show loading
                    CircularProgressIndicator()
                    Text(
                        text = "Initializing...",
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
