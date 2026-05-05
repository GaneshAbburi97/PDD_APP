package com.medical.fileprocessor.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.medical.fileprocessor.model.ProcessingStatus
import com.medical.fileprocessor.ui.components.StatusCard
import com.medical.fileprocessor.util.Constants
import com.medical.fileprocessor.util.NetworkManager
import com.medical.fileprocessor.util.Resource
import com.medical.fileprocessor.viewmodel.ResultViewModel
import kotlinx.coroutines.delay

/**
 * Screen that polls for the status of an active processing job.
 *
 * Improvements over the previous version:
 * - Animated progress bar showing estimated completion percentage
 * - Offline / backend-unavailable banner with retry action
 * - Smooth visual transitions between states
 */
@Composable
fun ProcessingScreen(
    jobId: String,
    onProcessingComplete: (String) -> Unit,
    viewModel: ResultViewModel = hiltViewModel(),
    networkManager: NetworkManager? = null,
) {
    val jobStatusState by viewModel.jobStatus.collectAsState()
    val pollingCounter = remember { mutableIntStateOf(0) }
    var estimatedProgress by remember { mutableFloatStateOf(0f) }
    var isOffline by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val animatedProgress by animateFloatAsState(
        targetValue = estimatedProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progress",
    )

    // Defensive check for empty jobId
    if (jobId.isBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Error: Invalid Job ID",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        return
    }

    // Polling loop with network awareness and progress estimation
    LaunchedEffect(jobId) {
        delay(500)

        var attempts = 0
        val maxAttempts = Constants.MAX_POLLING_ATTEMPTS

        while (attempts < maxAttempts) {
            isOffline = networkManager?.isConnected == false

            if (!isOffline) {
                viewModel.fetchJobStatus(jobId)
                pollingCounter.intValue = attempts + 1

                // Advance estimated progress smoothly (caps at 95 % until truly done)
                estimatedProgress = (attempts.toFloat() / maxAttempts.toFloat() * 95f).coerceAtMost(95f)

                delay(500)

                val currentStatus = jobStatusState?.dataOrNull?.status
                when (currentStatus) {
                    ProcessingStatus.COMPLETED -> {
                        estimatedProgress = 100f
                        delay(600) // Let the 100 % animation play
                        onProcessingComplete(jobId)
                        return@LaunchedEffect
                    }
                    ProcessingStatus.FAILED -> {
                        errorMessage = jobStatusState?.dataOrNull?.error ?: Constants.ERROR_PROCESSING_FAILED
                        return@LaunchedEffect
                    }
                    else -> Unit
                }
            } else {
                // Offline — pause polling and wait for reconnection
                delay(3_000)
            }

            attempts++
            delay(4_500)
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

            // ── Offline / backend-offline banner ─────────────────────────────
            if (isOffline) {
                OfflineBanner(
                    message = Constants.ERROR_NO_INTERNET,
                    onRetry = { isOffline = networkManager?.isConnected == false },
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = stringResource(com.medical.fileprocessor.R.string.processing_in_progress),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Progress bar ─────────────────────────────────────────────────
            LinearProgressIndicator(
                progress = { animatedProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${animatedProgress.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Job status card ───────────────────────────────────────────────
            when (val resource = jobStatusState) {
                is Resource.Success -> {
                    StatusCard(job = resource.data)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Poll attempt: ${pollingCounter.intValue}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                is Resource.Loading -> {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.connecting_server),
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                is Resource.Error -> {
                    Text(
                        text = stringResource(
                            com.medical.fileprocessor.R.string.status_check_error,
                            resource.message,
                        ),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = { viewModel.fetchJobStatus(jobId) },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text(stringResource(com.medical.fileprocessor.R.string.retry_button))
                    }
                }
                null -> {
                    CircularProgressIndicator()
                    Text(
                        text = "Initializing…",
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            // ── Terminal failure state ────────────────────────────────────────
            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Offline banner composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OfflineBanner(
    message: String,
    onRetry: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
            )
            TextButton(onClick = onRetry) {
                Text(
                    text = "Retry",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}
