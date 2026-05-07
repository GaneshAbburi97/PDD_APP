package com.medical.fileprocessor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilePickerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = Icons.Default.FileOpen,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = "Select Medical File")
    }
}
