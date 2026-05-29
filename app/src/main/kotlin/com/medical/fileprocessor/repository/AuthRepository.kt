package com.medical.fileprocessor.repository

import com.google.firebase.auth.FirebaseUser
import com.medical.fileprocessor.model.User
import com.medical.fileprocessor.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): FirebaseUser?
    fun login(email: String, password: String): Flow<Resource<User>>
    fun register(email: String, password: String, displayName: String): Flow<Resource<User>>
    fun logout()
}
