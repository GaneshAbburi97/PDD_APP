package com.medical.fileprocessor.model

/**
 * User model representing an authenticated user
 */
data class User(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null
)

