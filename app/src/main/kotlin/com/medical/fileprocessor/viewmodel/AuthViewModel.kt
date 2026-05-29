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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI State for Authentication status
 */
data class AuthUiState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val authStatus: Resource<User>? = null,
    val isLoggedIn: Boolean = false
)

/**
 * ViewModel for Auth flow
 * Manages user login, signup, current session, and logout states using StateFlow only.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkCurrentSession()
    }

    /**
     * Checks if a user session is active on startup.
     */
    fun checkCurrentSession() {
        val firebaseUser = authRepository.getCurrentUser()
        if (firebaseUser != null) {
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString()
            )
            _uiState.value = AuthUiState(
                currentUser = user,
                isLoggedIn = true,
                authStatus = Resource.Success(user)
            )
            Timber.tag("AUTH_VM").i("✅ Active user session detected: ${user.email}")
        } else {
            _uiState.value = AuthUiState(isLoggedIn = false)
            Timber.tag("AUTH_VM").d("No active user session detected")
        }
    }

    /**
     * Logs in the user with email and password.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, authStatus = resource)
                    }
                    is Resource.Success -> {
                        _uiState.value = AuthUiState(
                            currentUser = resource.data,
                            isLoggedIn = true,
                            authStatus = resource,
                            isLoading = false
                        )
                        Timber.tag("AUTH_VM").i("✅ Login succeeded: ${resource.data.email}")
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            authStatus = Resource.Error(resource.exception, resource.message)
                        )
                        Timber.tag("AUTH_VM").e(resource.exception, "❌ Login failed")
                    }
                }
            }
        }
    }

    /**
     * Registers a new user.
     */
    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            authRepository.register(email, password, displayName).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, authStatus = resource)
                    }
                    is Resource.Success -> {
                        _uiState.value = AuthUiState(
                            currentUser = resource.data,
                            isLoggedIn = true,
                            authStatus = resource,
                            isLoading = false
                        )
                        Timber.tag("AUTH_VM").i("✅ Registration succeeded: ${resource.data.email}")
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            authStatus = Resource.Error(resource.exception, resource.message)
                        )
                        Timber.tag("AUTH_VM").e(resource.exception, "❌ Registration failed")
                    }
                }
            }
        }
    }

    /**
     * Clears current user session.
     */
    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState(isLoggedIn = false)
        Timber.tag("AUTH_VM").i("User signed out successfully")
    }
}
