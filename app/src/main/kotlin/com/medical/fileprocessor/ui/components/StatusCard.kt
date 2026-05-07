package com.medical.fileprocessor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingStatus

/**
 * A component that displays the current status of a processing job.
 */
@Composable
fun StatusCard(
    job: ProcessingJob?,
    modifier: Modifier = Modifier
) {
    if (job == null) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "File: ${job.fileName}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status: ${job.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (job.status) {
                        ProcessingStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                        ProcessingStatus.FAILED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                if (job.status == ProcessingStatus.PROCESSING || job.status == ProcessingStatus.UPLOADING) {
                    Text(
                        text = "${job.progress}%",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            if (job.status == ProcessingStatus.PROCESSING || job.status == ProcessingStatus.UPLOADING) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { job.progress / 100f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            
            job.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Error: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
