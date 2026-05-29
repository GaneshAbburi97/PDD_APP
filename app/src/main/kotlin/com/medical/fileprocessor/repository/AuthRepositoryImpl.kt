package com.medical.fileprocessor.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.medical.fileprocessor.model.User
import com.medical.fileprocessor.network.ApiService
import com.medical.fileprocessor.network.LoginRequest
import com.medical.fileprocessor.network.RegisterRequest
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Robust implementation of AuthRepository that synchronizes both Firebase Auth and FastAPI authentication.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val apiService: ApiService
) : AuthRepository {

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            Timber.tag("AUTH").d("Logging in user: $email")
            // 1. Sign in to Firebase Auth
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Firebase authentication failed")

            // 2. Register/sync session with backend
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val apiUser = response.body()?.data
                if (apiUser != null) {
                    Timber.tag("AUTH").i("✅ Successful login on backend for ${apiUser.email}")
                    emit(Resource.Success(apiUser))
                } else {
                    emit(Resource.Success(User(firebaseUser.uid, email, firebaseUser.displayName, null)))
                }
            } else {
                Timber.tag("AUTH").w("Backend login response failed, falling back to Firebase Auth context")
                emit(Resource.Success(User(firebaseUser.uid, email, firebaseUser.displayName, null)))
            }
        } catch (e: Exception) {
            Timber.tag("AUTH").e(e, "❌ Login failed: ${e.message}")
            emit(Resource.Error(e))
        }
    }

    override fun register(email: String, password: String, displayName: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            Timber.tag("AUTH").d("Registering user: $email")
            // 1. Create user in Firebase Auth
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Firebase user creation failed")

            // 2. Call our FastAPI backend to register user details
            val request = RegisterRequest(email, password, displayName)
            val response = apiService.register(request)

            if (response.isSuccessful && response.body()?.success == true) {
                val apiUser = response.body()?.data
                if (apiUser != null) {
                    Timber.tag("AUTH").i("✅ Successful backend registration for ${apiUser.email}")
                    emit(Resource.Success(apiUser))
                } else {
                    emit(Resource.Success(User(firebaseUser.uid, email, displayName, null)))
                }
            } else {
                Timber.tag("AUTH").w("Backend registration response failed, falling back to Firebase Auth context")
                emit(Resource.Success(User(firebaseUser.uid, email, displayName, null)))
            }
        } catch (e: Exception) {
            Timber.tag("AUTH").e(e, "❌ Registration failed: ${e.message}")
            emit(Resource.Error(e))
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
        Timber.tag("AUTH").i("User signed out successfully")
    }
}
