package com.example.tmdapp.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val _currentUserId = MutableStateFlow<String?>(getStoredUserId())
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    fun saveUserId(userId: String) {
        saveSession(userId, getStoredAuthToken())
    }

    fun saveSession(userId: String, authToken: String?) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_AUTH_TOKEN, authToken)
            .apply()
        _currentUserId.value = userId
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_AUTH_TOKEN)
            .apply()
        _currentUserId.value = null
    }

    private fun getStoredUserId(): String? {
        return try {
            prefs.getString(KEY_USER_ID, null)
        } catch (e: ClassCastException) {
            // Handle legacy integer IDs by clearing them
            prefs.edit().remove(KEY_USER_ID).apply()
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return getStoredUserId() != null && getStoredAuthToken() != null
    }

    fun getStoredAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    companion object {
        private const val PREF_NAME = "TMD_SESSION"
        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_AUTH_TOKEN = "AUTH_TOKEN"
    }
}
