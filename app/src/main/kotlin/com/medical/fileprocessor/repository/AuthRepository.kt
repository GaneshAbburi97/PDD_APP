package com.medical.fileprocessor.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    fun getCurrentUser(): FirebaseUser?
}
