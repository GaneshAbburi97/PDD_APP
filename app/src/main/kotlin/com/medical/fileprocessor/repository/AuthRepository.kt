package com.medical.fileprocessor.repository

import com.medical.fileprocessor.model.User
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining authentication operations.
 * 
 * Using an interface allows for multiple implementations (e.g., Firebase or Mock)
 * and facilitates unit testing.
 */
interface AuthRepository {
    
    /**
     * Authenticates a user with email and password.
     * @return A Flow emitting [Resource] states containing the [User].
     */
    fun login(email: String, password: String): Flow<Resource<User>>
    
    /**
     * Registers a new user account.
     * @return A Flow emitting [Resource] states containing the newly created [User].
     */
    fun register(email: String, password: String, displayName: String): Flow<Resource<User>>
    
    /**
     * Logs the current user out of the system.
     */
    fun logout()
    
    /**
     * Retrieves the currently authenticated user, if any.
     * @return The current [User] or null if no one is logged in.
     */
    fun getCurrentUser(): User?
}
