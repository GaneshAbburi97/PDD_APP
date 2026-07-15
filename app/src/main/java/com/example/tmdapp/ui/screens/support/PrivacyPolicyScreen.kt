package com.example.tmdapp.ui.screens.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Privacy Policy for TMD Care AI",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Last Updated: Oct 2024",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            PolicySection(
                "1. Data Collection",
                "We collect personal information such as your name, email address, and health data (pain logs, sleep records, and wellness assessments) to provide personalized care and insights."
            )
            PolicySection(
                "2. Use of Data",
                "Your data is used to analyze your recovery progress, generate health reports, and provide AI-driven recommendations. We do not sell your personal data to third parties."
            )
            PolicySection(
                "3. Data Storage",
                "Your information is stored securely using industry-standard encryption. We use a custom secure backend for authentication and database management."
            )
            PolicySection(
                "4. Your Rights",
                "You have the right to access, update, or delete your personal information at any time through the Profile and Settings sections of the app."
            )
            PolicySection(
                "5. Contact Us",
                "If you have any questions about this Privacy Policy, please contact us at support@tmdcare.ai"
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(content, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
    }
}
