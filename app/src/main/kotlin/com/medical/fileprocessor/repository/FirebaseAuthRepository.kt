package com.medical.fileprocessor.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.medical.fileprocessor.model.User
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of AuthRepository
 * 
 * Handles user authentication using Firebase Authentication service.
 * Features:
 * - Email/password registration and login
 * - Session persistence (automatic)
 * - Logout functionality
 * - User data retrieval
 * - Comprehensive error handling
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    /**
     * Authenticates a user with email and password.
     * 
     * Flow:
     * 1. Emit Loading state
     * 2. Call Firebase signInWithEmailAndPassword()
     * 3. On success: emit User data with UID and email
     * 4. On error: emit specific error message
     * 
     * @param email User's email address
     * @param password User's password
     * @return Flow emitting Resource<User> with auth state
     */
    override fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            Timber.tag("AUTH").d("🔐 Attempting login for email: $email")

            // Firebase sign in
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val fbUser = authResult.user

            if (fbUser != null) {
                val user = User(
                    id = fbUser.uid,
                    email = fbUser.email ?: "",
                    displayName = fbUser.displayName,
                    token = fbUser.getIdToken(false).await().token
                )
                Timber.tag("AUTH").i("✅ Login successful for: ${fbUser.email}")
                emit(Resource.Success(user))
            } else {
                throw Exception("User data is null after login")
            }

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            val errorMsg = "Invalid email or password"
            Timber.tag("AUTH").w("❌ $errorMsg")
            emit(Resource.Error(e, message = errorMsg))

        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Login failed"
            Timber.tag("AUTH").e(e, "❌ Login error: $errorMsg")
            emit(Resource.Error(e, message = errorMsg))
        }
    }

    /**
     * Registers a new user account.
     * 
     * Flow:
     * 1. Emit Loading state
     * 2. Call Firebase createUserWithEmailAndPassword()
     * 3. Update user profile with displayName
     * 4. On success: emit User data
     * 5. On error: emit specific error message
     * 
     * @param email User's email address
     * @param password User's password (minimum 6 characters for Firebase)
     * @param displayName User's display name
     * @return Flow emitting Resource<User> with auth state
     */
    override fun register(email: String, password: String, displayName: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            Timber.tag("AUTH").d("📝 Attempting registration for email: $email")

            // Firebase create user
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val fbUser = authResult.user

            if (fbUser != null) {
                // Update display name
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                
                fbUser.updateProfile(profileUpdates).await()

                val user = User(
                    id = fbUser.uid,
                    email = fbUser.email ?: "",
                    displayName = displayName,
                    token = fbUser.getIdToken(false).await().token
                )
                Timber.tag("AUTH").i("✅ Registration successful for: ${fbUser.email}")
                emit(Resource.Success(user))
            } else {
                throw Exception("User data is null after registration")
            }

        } catch (e: FirebaseAuthWeakPasswordException) {
            val errorMsg = "Password is too weak (minimum 6 characters)"
            Timber.tag("AUTH").w("❌ $errorMsg")
            emit(Resource.Error(e, message = errorMsg))

        } catch (e: FirebaseAuthUserCollisionException) {
            val errorMsg = "Email already registered"
            Timber.tag("AUTH").w("❌ $errorMsg")
            emit(Resource.Error(e, message = errorMsg))

        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Registration failed"
            Timber.tag("AUTH").e(e, "❌ Registration error: $errorMsg")
            emit(Resource.Error(e, message = errorMsg))
        }
    }

    /**
     * Logs the current user out of the system.
     * 
     * This clears:
     * - Firebase authentication session
     * - Cached credentials
     * - Local user state
     * 
     * No network call needed - Firebase manages session locally.
     */
    override fun logout() {
        try {
            Timber.tag("AUTH").i("🚪 Logging out user")
            firebaseAuth.signOut()
            Timber.tag("AUTH").i("✅ Logout successful")
        } catch (e: Exception) {
            Timber.tag("AUTH").e(e, "❌ Logout error: ${e.localizedMessage}")
        }
    }

    /**
     * Retrieves the currently authenticated user, if any.
     * 
     * This checks Firebase's cached session:
     * - If user is logged in: returns User object
     * - If user is NOT logged in: returns null
     * 
     * This is used for:
     * - Checking if user needs to login
     * - Session persistence after app restart
     * - Pre-populating user data
     * 
     * @return User object or null if not authenticated
     */
    override fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.let { fbUser ->
            User(
                id = fbUser.uid,
                email = fbUser.email ?: "",
                displayName = fbUser.displayName
            )
        }.also { user ->
            if (user != null) {
                Timber.tag("AUTH").d("👤 Current user: ${user.email}")
            } else {
                Timber.tag("AUTH").d("👤 No current user (not logged in)")
            }
        }
    }
}
