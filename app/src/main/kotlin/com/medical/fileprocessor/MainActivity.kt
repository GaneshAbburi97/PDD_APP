package com.medical.fileprocessor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.medical.fileprocessor.ui.screens.ProcessingScreen
import com.medical.fileprocessor.ui.screens.UploadScreen
import com.medical.fileprocessor.util.Constants
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
    NavHost(
        navController = navController,
        startDestination = Constants.ROUTE_UPLOAD
    ) {
        composable(Constants.ROUTE_UPLOAD) {
            UploadScreen(
                onNavigateToProcessing = { jobId ->
                    navController.navigate(
                        Constants.ROUTE_PROCESSING.replace("{jobId}", jobId)
                    )
                },
                onLogout = {
                    // Handle logout if needed
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
                onProcessingComplete = {
                    // Handle processing complete if needed
                }
            )
        }
    }
}
