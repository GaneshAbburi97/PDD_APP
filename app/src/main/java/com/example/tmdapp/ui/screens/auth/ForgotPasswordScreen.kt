package com.example.tmdapp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tmdapp.TmdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: TmdViewModel,
    onNavigateBack: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (step == 1) {
                Text(
                    text = "Enter your email address to receive an OTP for password reset.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            viewModel.forgotPassword(email, onSuccess = {
                                message = ""
                                isError = false
                                step = 2
                            }, onError = {
                                message = it
                                isError = true
                            })
                        } else {
                            message = "Please enter your email"
                            isError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Send OTP")
                }
            } else if (step == 2) {
                Text(
                    text = "Enter the 6-digit OTP sent to your email.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("6-digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        if (otp.isNotBlank()) {
                            viewModel.verifyResetOtp(email, otp, onSuccess = {
                                message = ""
                                isError = false
                                step = 3
                            }, onError = {
                                message = it
                                isError = true
                            })
                        } else {
                            message = "Please enter the OTP"
                            isError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Verify OTP")
                }
            } else if (step == 3) {
                Text(
                    text = "Enter and confirm your new password.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        if (newPassword == confirmPassword && newPassword.isNotBlank()) {
                            viewModel.setNewPassword(email, newPassword, onSuccess = {
                                message = "Password reset successfully!"
                                isError = false
                                onNavigateBack()
                            }, onError = {
                                message = it
                                isError = true
                            })
                        } else {
                            message = "Passwords do not match or are empty"
                            isError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Reset Password")
                }
            }
        }
    }
}
