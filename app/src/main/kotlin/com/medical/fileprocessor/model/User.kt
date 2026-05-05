package com.medical.fileprocessor.model

import java.io.Serializable

/**
 * User model representing an authenticated user.
 */
data class User(
    val id: String,
    val email: String,
    val token: String? = null,
    val displayName: String? = null,
    val profilePhotoUrl: String? = null,
) : Serializable
