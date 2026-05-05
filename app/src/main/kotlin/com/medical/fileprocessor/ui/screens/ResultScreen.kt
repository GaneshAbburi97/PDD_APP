package com.medical.fileprocessor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.medical.fileprocessor.ui.components.ResultViewer
import com.medical.fileprocessor.util.Resource
import com.medical.fileprocessor.viewmodel.ResultViewModel

/**
 * Screen for displaying the final analysis results of a processed medical file.
 */
@Composable
fun ResultScreen(
    jobId: String,
    onNavigateBack: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val resultState by viewModel.processingResult.collectAsState()
    val uriHandler = LocalUriHandler.current

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

    LaunchedEffect(jobId) {
        viewModel.fetchResult(jobId)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            Text(
                text = stringResource(com.medical.fileprocessor.R.string.results_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            
            Text(
                text = stringResource(com.medical.fileprocessor.R.string.job_id_label, jobId),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp),
            )

            when (val resource = resultState) {
                is Resource.Success -> {
                    val result = resource.data
                    
                    // Reusable Result Viewer Component
                    ResultViewer(
                        result = result,
                    ) { url -> uriHandler.openUri(url) }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.model_findings_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    
                    // Display metadata
                    if (result.metadata.isEmpty()) {
                        Text(
                            text = stringResource(com.medical.fileprocessor.R.string.no_metadata_available),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        result.metadata.forEach { (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = key, style = MaterialTheme.typography.bodyMedium)
                                Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { /* Implement Download Logic */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                    ) {
                        Text(stringResource(com.medical.fileprocessor.R.string.download_processed_file))
                    }
                }
                is Resource.Loading -> {
                    CircularProgressIndicator()
                    Text(
                        stringResource(com.medical.fileprocessor.R.string.fetching_results),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                is Resource.Error -> {
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.load_results_error, resource.message),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                null -> {
                    CircularProgressIndicator()
                    Text(
                        "Loading results...",
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                Text(stringResource(com.medical.fileprocessor.R.string.back_to_dashboard))
            }
        }
    }
}
