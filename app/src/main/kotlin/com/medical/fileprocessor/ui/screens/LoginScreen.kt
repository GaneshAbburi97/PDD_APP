package com.medical.fileprocessor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.medical.fileprocessor.viewmodel.AuthViewModel

/**
 * Login Screen for user authentication.
 * 
 * @param onLoginSuccess Callback triggered when authentication is successful.
 * @param onNavigateToRegister Callback to navigate to the registration screen.
 * @param viewModel The [AuthViewModel] to handle logic and state.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val authState by viewModel.authState.collectAsState()

    // Handle navigation side effect on success
    LaunchedEffect(authState) {
        if (authState?.isSuccess == true) {
            onLoginSuccess()
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
            Text(
                text = stringResource(com.medical.fileprocessor.R.string.login_welcome),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            
            Text(
                text = stringResource(com.medical.fileprocessor.R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
            )

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(com.medical.fileprocessor.R.string.email_label)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                isError = authState?.isError == true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(com.medical.fileprocessor.R.string.password_label)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                singleLine = true,
                isError = authState?.isError == true
            )

            // Error Message
            if (authState?.isError == true) {
                val errorMsg = authState?.exceptionOrNull()?.localizedMessage ?: stringResource(com.medical.fileprocessor.R.string.loading)
                Text(
                    text = stringResource(com.medical.fileprocessor.R.string.login_failed, errorMsg),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.Start),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = (authState?.isLoading != true) && email.isNotBlank() && password.isNotBlank(),
            ) {
                if (authState?.isLoading == true) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(stringResource(com.medical.fileprocessor.R.string.login_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Registration Link
            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(com.medical.fileprocessor.R.string.no_account_link))
            }
        }
    }
}
