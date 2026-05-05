package com.medical.fileprocessor.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.medical.fileprocessor.ui.components.FilePickerButton
import com.medical.fileprocessor.ui.components.UploadProgressBar
import com.medical.fileprocessor.util.Resource
import com.medical.fileprocessor.util.getFileName
import com.medical.fileprocessor.viewmodel.UploadViewModel

/**
 * Screen for selecting and uploading medical files for processing.
 * 
 * @param onNavigateToProcessing Callback to navigate to the status polling screen.
 * @param onLogout Callback to log out and return to the login screen.
 * @param viewModel The [UploadViewModel] orchestrating the upload flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onNavigateToProcessing: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: UploadViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Launcher for the system file picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.getFileName(context) ?: "unknown_file.nii"
            viewModel.onFileSelected(it, fileName)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(com.medical.fileprocessor.R.string.upload_screen_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            
            Text(
                text = stringResource(com.medical.fileprocessor.R.string.upload_screen_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp),
            )

            // Reusable File Picker Component
            FilePickerButton(
                onClick = { launcher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.status?.isLoading != true,
            )
            
            // Show selected filename if available
            uiState.fileName?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.selected_file_label, it),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = { viewModel.startUploadAndProcess() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = (uiState.selectedFileUri != null) && (uiState.status?.isLoading != true),
            ) {
                Text(stringResource(com.medical.fileprocessor.R.string.start_processing_button))
            }

            // Progress and Status UI
            Spacer(modifier = Modifier.height(24.dp))
            
            when (val status = uiState.status) {
                is Resource.Loading -> {
                    // Reusable Progress Bar Component
                    UploadProgressBar(
                        progress = 0.5f, // Note: In a real app, bind to actual upload percentage
                        fileName = uiState.fileName ?: "",
                    )
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.preparing_analysis),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                is Resource.Error -> {
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.upload_error, status.message),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                is Resource.Success -> {
                    // Navigation logic handled by ViewModel state update
                    LaunchedEffect(status.data.jobId) {
                        onNavigateToProcessing(status.data.jobId)
                        viewModel.resetState() // Clean up for next upload
                    }
                }
                null -> { /* Ready to start */ }
            }
        }
    }
}
