package com.medical.fileprocessor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medical.fileprocessor.model.User
import com.medical.fileprocessor.repository.AuthRepository
import com.medical.fileprocessor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for authentication operations.
 * 
 * Responsibilities:
 * - Handle login/signup/logout
 * - Manage auth state
 * - Persist session
 * - Expose auth state to UI
 * 
 * State Management:
 * Uses StateFlow for reactive UI updates.
 * Emits: Loading -> Success/Error -> completed
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // =====================================================================
    // STATE MANAGEMENT
    // =====================================================================

    // Login/Register result state
    private val _authState = MutableStateFlow<Resource<User>>(Resource.Loading())
    val authState: StateFlow<Resource<User>> = _authState.asStateFlow()

    // Current user state (checked on app start)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Is user checking for existing session?
    private val _isCheckingSession = MutableStateFlow(false)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession.asStateFlow()

    // =====================================================================
    // LIFECYCLE / INITIALIZATION
    // =====================================================================

    init {
        Timber.tag("AUTH_VM").d("🆕 AuthViewModel initialized")
        checkExistingSession()
    }

    /**
     * Checks if user is already logged in (session persistence).
     * 
     * Called on app start or ViewModel creation.
     * 
     * Flow:
     * 1. Check Firebase Auth current user
     * 2. If user exists: emit as current user
     * 3. If not: current user remains null
     * 4. Skip login screen if user exists
     * 
     * This enables auto-login without user re-entering credentials.
     */
    private fun checkExistingSession() {
        viewModelScope.launch {
            try {
                _isCheckingSession.value = true
                Timber.tag("AUTH_VM").d("🔍 Checking for existing session...")

                val user = authRepository.getCurrentUser()
                
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = Resource.Success(user)
                    Timber.tag("AUTH_VM").i("✅ Session found for: ${user.email}")
                } else {
                    _currentUser.value = null
                    Timber.tag("AUTH_VM").d("ℹ️ No existing session")
                }

            } catch (e: Exception) {
                Timber.tag("AUTH_VM").e(e, "❌ Error checking session: ${e.localizedMessage}")
                _currentUser.value = null
            } finally {
                _isCheckingSession.value = false
            }
        }
    }

    /**
     * Logs in user with email and password.
     * 
     * @param email User's email
     * @param password User's password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                Timber.tag("AUTH_VM").d("🔐 Login started for: $email")
                
                authRepository.login(email, password).collect { resource ->
                    _authState.value = resource
                    
                    if (resource is Resource.Success) {
                        _currentUser.value = resource.data
                        Timber.tag("AUTH_VM").i("✅ Login successful")
                    } else if (resource is Resource.Error) {
                        Timber.tag("AUTH_VM").w("❌ Login failed: ${resource.message}")
                    }
                }

            } catch (e: Exception) {
                Timber.tag("AUTH_VM").e(e, "❌ Login exception: ${e.localizedMessage}")
                _authState.value = Resource.Error(e)
            }
        }
    }

    /**
     * Registers new user.
     * 
     * @param email User's email
     * @param password User's password
     * @param displayName User's display name
     */
    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            try {
                Timber.tag("AUTH_VM").d("📝 Registration started for: $email")
                
                authRepository.register(email, password, displayName).collect { resource ->
                    _authState.value = resource
                    
                    if (resource is Resource.Success) {
                        _currentUser.value = resource.data
                        Timber.tag("AUTH_VM").i("✅ Registration successful")
                    } else if (resource is Resource.Error) {
                        Timber.tag("AUTH_VM").w("❌ Registration failed: ${resource.message}")
                    }
                }

            } catch (e: Exception) {
                Timber.tag("AUTH_VM").e(e, "❌ Registration exception: ${e.localizedMessage}")
                _authState.value = Resource.Error(e)
            }
        }
    }

    /**
     * Logs out current user.
     * 
     * Clears:
     * - Firebase Auth session
     * - Current user state
     * - Auth state
     */
    fun logout() {
        try {
            Timber.tag("AUTH_VM").d("🚪 Logout initiated")
            authRepository.logout()
            _currentUser.value = null
            _authState.value = Resource.Loading()
            Timber.tag("AUTH_VM").i("✅ Logout successful")
        } catch (e: Exception) {
            Timber.tag("AUTH_VM").e(e, "❌ Logout error: ${e.localizedMessage}")
        }
    }

    /**
     * Clears auth state for UI refresh.
     * Useful for clearing error messages.
     */
    fun clearAuthState() {
        _authState.value = Resource.Loading()
    }
}
