package com.example.tmdapp.data.repository

import com.example.tmdapp.data.model.User
import com.example.tmdapp.data.remote.ApiClient
import com.example.tmdapp.data.remote.GoogleLoginRequest
import com.example.tmdapp.data.remote.UpdateProfileRequest

class AuthRepository {
    private val api = ApiClient.apiService
    private var currentUser: User? = null

    suspend fun signUp(name: String, email: String, passwordRaw: String) {
        val request = com.example.tmdapp.data.remote.RegisterRequest(name, email, passwordRaw)
        val response = api.register(request)
        ApiClient.authToken = response.token
        currentUser = response.user
        android.util.Log.d("AuthRepository", "Backend register successful")
    }

    suspend fun login(email: String, passwordRaw: String): Boolean {
        return try {
            val request = com.example.tmdapp.data.remote.LoginRequest(email, passwordRaw)
            val response = api.login(request)
            ApiClient.authToken = response.token
            currentUser = response.user
            android.util.Log.d("AuthRepository", "Backend login successful")
            true
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Backend login failed", e)
            false
        }
    }
    
    suspend fun verifyEmailOtp(email: String, token: String): Boolean {
        return false // Not implemented
    }

    suspend fun loginWithGoogle(idToken: String): Boolean {
        android.util.Log.d("AuthRepository", "Attempting login with ID Token to new backend: ${idToken.take(10)}...")
        return try {
            val response = api.googleLogin(GoogleLoginRequest(idToken))
            ApiClient.authToken = response.token
            currentUser = response.user
            android.util.Log.d("AuthRepository", "Backend Google login successful")
            true
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Backend Google login failed", e)
            false
        }
    }

    suspend fun getCurrentUser(): User? {
        if (currentUser != null) return currentUser
        return try {
            if (ApiClient.authToken == null) return null
            currentUser = api.getProfile()
            currentUser
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(userId: String, name: String, email: String, imagePath: String?, heightCm: Float? = null, weightKg: Float? = null): Boolean {
        return try {
            currentUser = api.updateProfile(UpdateProfileRequest(name, email, imagePath, heightCm, weightKg))
            true
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Profile update failed", e)
            false
        }
    }

    suspend fun deleteAccount(userId: String): Boolean {
        return try {
            api.deleteProfile()
            ApiClient.authToken = null
            currentUser = null
            true
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Account deletion failed", e)
            false
        }
    }

    suspend fun logout() {
        try {
            ApiClient.authToken = null
            currentUser = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
