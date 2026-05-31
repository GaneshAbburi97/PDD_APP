package com.medical.fileprocessor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.medical.fileprocessor.ui.screens.LoginScreen
import com.medical.fileprocessor.ui.screens.ProcessingScreen
import com.medical.fileprocessor.ui.screens.RegisterScreen
import com.medical.fileprocessor.ui.screens.UploadScreen
import com.medical.fileprocessor.ui.screens.ResultScreen
import com.medical.fileprocessor.util.Constants
import com.medical.fileprocessor.util.Resource
import com.medical.fileprocessor.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the Medical File Processor app.
 * Uses Jetpack Compose for UI and Hilt for Dependency Injection.
 * 
 * NAVIGATION FLOW:
 * 1. Start at UploadScreen (ROUTE_UPLOAD)
 * 2. On upload success, navigate to ProcessingScreen (ROUTE_PROCESSING)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val authError = (authState.authStatus as? Resource.Error)?.message

    if (!authState.isSessionChecked) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.semantics { contentDescription = "Checking authentication session" }
                )
            }
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) Constants.ROUTE_UPLOAD else Constants.ROUTE_LOGIN
    ) {
        composable(Constants.ROUTE_LOGIN) {
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    navController.navigate(Constants.ROUTE_UPLOAD) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                    }
                }
            }
            LoginScreen(
                isLoading = authState.isLoading,
                errorMessage = authError,
                onLogin = { email, password -> authViewModel.login(email, password) },
                onNavigateToRegister = { navController.navigate(Constants.ROUTE_REGISTER) }
            )
        }

        composable(Constants.ROUTE_REGISTER) {
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    navController.navigate(Constants.ROUTE_UPLOAD) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                    }
                }
            }
            RegisterScreen(
                isLoading = authState.isLoading,
                errorMessage = authError,
                onRegister = { email, password, displayName ->
                    authViewModel.register(email, password, displayName)
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Constants.ROUTE_UPLOAD) {
            LaunchedEffect(authState.isLoggedIn) {
                if (!authState.isLoggedIn) {
                    navController.navigate(Constants.ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            UploadScreen(
                onNavigateToProcessing = { jobId ->
                    navController.navigate(
                        Constants.ROUTE_PROCESSING.replace("{jobId}", jobId)
                    )
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Constants.ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Constants.ROUTE_PROCESSING,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            ProcessingScreen(
                jobId = jobId,
                onProcessingComplete = { completedJobId ->
                    navController.navigate(
                        Constants.ROUTE_RESULT.replace("{jobId}", completedJobId)
                    ) {
                        popUpTo(Constants.ROUTE_UPLOAD) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Constants.ROUTE_RESULT,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            ResultScreen(
                jobId = jobId,
                onNavigateBack = {
                    navController.navigate(Constants.ROUTE_UPLOAD) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
