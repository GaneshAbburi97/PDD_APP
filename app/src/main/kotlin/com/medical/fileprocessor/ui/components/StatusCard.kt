package com.medical.fileprocessor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.medical.fileprocessor.model.ProcessingJob
import com.medical.fileprocessor.model.ProcessingStatus

/**
 * A reusable card component to display the status and progress of a processing job.
 * 
 * @param job The [ProcessingJob] containing current status and progress data.
 */
@Composable
fun StatusCard(job: ProcessingJob) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.fileName ?: stringResource(com.medical.fileprocessor.R.string.unknown_file),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(com.medical.fileprocessor.R.string.job_id_label, job.jobId),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                StatusIcon(status = job.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(com.medical.fileprocessor.R.string.status_label, job.status.displayName),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                Text(
                    text = "${job.progress}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            LinearProgressIndicator(
                progress = { job.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(8.dp),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                color = when (job.status) {
                    ProcessingStatus.FAILED -> MaterialTheme.colorScheme.error
                    ProcessingStatus.COMPLETED -> Color(0xFF4CAF50) // Success Green
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            
            if ((job.status == ProcessingStatus.FAILED) && (job.error != null)) {
                Text(
                    text = stringResource(com.medical.fileprocessor.R.string.status_check_error, job.error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusIcon(status: ProcessingStatus) {
    val (icon, color) = when (status) {
        ProcessingStatus.COMPLETED -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
        ProcessingStatus.FAILED -> Icons.Default.Warning to MaterialTheme.colorScheme.error
        else -> Icons.Default.Info to MaterialTheme.colorScheme.primary
    }
    
    Icon(
        imageVector = icon,
        contentDescription = status.displayName,
        tint = color,
        modifier = Modifier.size(24.dp)
    )
}
