package com.medical.fileprocessor.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.medical.fileprocessor.ui.screens.*
import com.medical.fileprocessor.util.Constants
import com.medical.fileprocessor.viewmodel.AuthViewModel

/**
 * Main Navigation Graph for the Medical File Processor app.
 * 
 * Handles routing between:
 * - Authentication (login/register)
 * - File upload (main app)
 * - Processing results
 * - Session persistence (auto-login)
 */
@Composable
fun NavGraph(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    
    // Observe session state
    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Determine start destination based on session
    val startDestination = when {
        isCheckingSession -> Constants.ROUTE_LOGIN  // Show loading on login screen
        currentUser != null -> Constants.ROUTE_UPLOAD  // User is logged in
        else -> Constants.ROUTE_LOGIN  // No user - show login screen
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize(),
    ) {
        // --- 1. Authentication Flow ---
        composable(
            route = Constants.ROUTE_LOGIN,
        ) {
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to Upload and clear the auth stack safely
                    navController.navigate(Constants.ROUTE_UPLOAD) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Constants.ROUTE_REGISTER) {
                        launchSingleTop = true
                    }
                },
            )
        }
        
        composable(Constants.ROUTE_REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Automatically move to main app after registration
                    navController.navigate(Constants.ROUTE_UPLOAD) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
            )
        }
        
        // --- 2. File Selection Flow ---
        composable(Constants.ROUTE_UPLOAD) {
            UploadScreen(
                onNavigateToProcessing = { jobId ->
                    // Pass the job ID to the processing screen
                    val route = Constants.ROUTE_PROCESSING.replace("{jobId}", jobId)
                    navController.navigate(route)
                },
                onLogout = {
                    // Handle logout
                    authViewModel.logout()
                    navController.navigate(Constants.ROUTE_LOGIN) {
                        popUpTo(Constants.ROUTE_UPLOAD) { inclusive = true }
                    }
                },
            )
        }
        
        // --- 3. Processing & Results Flow ---
        composable(
            route = Constants.ROUTE_PROCESSING,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            
            if (jobId.isEmpty()) {
                // If jobId is missing, redirect back to upload
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_UPLOAD) {
                        popUpTo(Constants.ROUTE_UPLOAD) { inclusive = true }
                    }
                }
            } else {
                ProcessingScreen(
                    jobId = jobId,
                    onProcessingComplete = { completedJobId ->
                        // Navigate to Result screen and remove processing from stack
                        val route = Constants.ROUTE_RESULT.replace("{jobId}", completedJobId)
                        navController.navigate(route) {
                            popUpTo(Constants.ROUTE_UPLOAD) { inclusive = false }
                        }
                    },
                )
            }
        }
        
        composable(
            route = Constants.ROUTE_RESULT,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            
            if (jobId.isEmpty()) {
                // If jobId is missing, redirect back to upload
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_UPLOAD) {
                        popUpTo(Constants.ROUTE_UPLOAD) { inclusive = true }
                    }
                }
            } else {
                ResultScreen(
                    jobId = jobId,
                    onNavigateBack = {
                        // Go back to the upload screen instead of popping everything
                        navController.navigate(Constants.ROUTE_UPLOAD) {
                            popUpTo(Constants.ROUTE_UPLOAD) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
