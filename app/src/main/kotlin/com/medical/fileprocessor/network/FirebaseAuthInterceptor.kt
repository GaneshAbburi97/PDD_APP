package com.medical.fileprocessor.network

import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * OkHttp Interceptor that automatically injects Firebase ID tokens into every request.
 *
 * SECURITY:
 * - Adds "Authorization: Bearer {token}" header to all requests
 * - Authenticates with Firebase backend
 * - Tokens auto-refreshed by Firebase SDK
 * - Failed auth causes 401 Unauthorized from backend
 *
 * PERFORMANCE:
 * - Minimal overhead (one token header addition)
 * - Reuses Firebase internal token cache
 * - No extra network calls (tokens cached by SDK)
 */
class FirebaseAuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            try {
                // Get cached ID token (synchronous, doesn't block network)
                // Firebase SDK handles refresh automatically
                currentUser.getIdToken(false).addOnSuccessListener { tokenResult ->
                    val token = tokenResult?.token
                    if (!token.isNullOrEmpty()) {
                        Timber.tag("AUTH_INT").d("✅ Added auth token to request")
                    }
                }.addOnFailureListener { e ->
                    Timber.tag("AUTH_INT").w("⚠️ Failed to get auth token: ${e.message}")
                }

                // For synchronous interceptor, we need to use the blocking call
                val tokenTask = currentUser.getIdToken(false)
                // This will block briefly but necessary for Interceptor interface
                val timeoutMs = 5000L // 5 second timeout
                var attempts = 0
                while (!tokenTask.isComplete && attempts < timeoutMs / 100) {
                    Thread.sleep(100)
                    attempts++
                }

                val token = tokenTask.result?.token
                if (!token.isNullOrEmpty()) {
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    Timber.tag("AUTH_INT").d("✅ Auth token injected (${token.length} chars)")
                }
            } catch (e: Exception) {
                Timber.tag("AUTH_INT").e(e, "❌ Failed to inject auth token: ${e.message}")
                // Continue without token - backend will reject with 401
            }
        } else {
            Timber.tag("AUTH_INT").w("⚠️ No user signed in, proceeding without auth")
        }

        return chain.proceed(request)
    }
}

