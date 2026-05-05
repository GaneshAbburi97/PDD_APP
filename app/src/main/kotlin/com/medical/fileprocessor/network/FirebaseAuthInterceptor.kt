package com.medical.fileprocessor.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * OkHttp Interceptor for Firebase Authentication
 * 
 * Automatically adds Firebase ID token to every Retrofit request.
 * This token authenticates the user to the backend API.
 * 
 * Pattern:
 * Every API request gets header: Authorization: Bearer <firebase_id_token>
 * 
 * The backend validates this token to:
 * - Verify user identity
 * - Extract user UID
 * - Authorize access to user's resources
 */
class FirebaseAuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    /**
     * Intercepts outgoing HTTP requests and adds Firebase authentication token.
     * 
     * Flow:
     * 1. Get the original request
     * 2. Check if user is authenticated
     * 3. If authenticated: get Firebase ID token
     * 4. Add Authorization header with token
     * 5. Send request to backend
     * 6. Return response
     * 
     * @param chain The interceptor chain
     * @return Response from the server
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Get current user
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            try {
                // Get Firebase ID token (blocking call - safe in interceptor)
                val tokenResult = currentUser.getIdToken(false).waitForResult()
                val token = tokenResult?.token

                if (!token.isNullOrEmpty()) {
                    // Add Authorization header
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()

                    Timber.tag("NETWORK").d("🔐 Added auth token to request: ${request.url}")
                } else {
                    Timber.tag("NETWORK").w("⚠️ Firebase token is null or empty")
                }

            } catch (e: Exception) {
                Timber.tag("NETWORK").e(e, "❌ Failed to get Firebase token: ${e.localizedMessage}")
                // Continue without token - let backend handle 401
            }
        } else {
            Timber.tag("NETWORK").d("ℹ️ No authenticated user - request sent without auth token")
        }

        // Proceed with the request
        return chain.proceed(request)
    }
}

/**
 * Extension function to wait for Firebase Task result synchronously.
 * 
 * This is safe to call from OkHttp interceptor (not on main thread).
 * 
 * @return The task result or null if failed
 */
private fun <T> com.google.android.gms.tasks.Task<T>.waitForResult(): T? {
    return try {
        kotlinx.coroutines.runBlocking {
            this@waitForResult.await()
        }
    } catch (e: Exception) {
        null
    }
}
