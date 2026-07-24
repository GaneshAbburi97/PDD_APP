package com.example.tmdapp.ui.screens.support

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tmdapp.TmdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Contact Support",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Need help or have feedback?",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Reach out to our support team directly. We are happy to assist you!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:ganeshabburi97@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "TMD Care AI App Support")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Contact Support")
            }
        }
    }
}
