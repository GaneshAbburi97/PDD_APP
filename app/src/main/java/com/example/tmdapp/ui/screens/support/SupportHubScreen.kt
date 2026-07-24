package com.example.tmdapp.ui.screens.support

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportHubScreen(
    onNavigateToHelp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Support") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Help Center Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { onNavigateToHelp() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpCenter,
                        contentDescription = "Help Center",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Help Center",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
