package com.medical.fileprocessor.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Firebase dependencies.
 * 
 * Provides singleton instances of Firebase services (Auth, Storage, Firestore)
 * to be injected throughout the application.
 * 
 * Benefits of this module:
 * - Centralized Firebase configuration
 * - Easy to mock Firebase for testing
 * - Lazy initialization of Firebase instances
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides Firebase Authentication instance.
     * 
     * Firebase Auth handles user registration, login, and session management.
     * 
     * @return Singleton FirebaseAuth instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    /**
     * Provides Firebase Storage instance.
     * 
     * Firebase Storage handles file uploads and downloads.
     * Configured with your project's storage bucket from google-services.json
     * 
     * @return Singleton FirebaseStorage instance
     */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }

    /**
     * Provides Firebase Firestore instance.
     * 
     * Firestore is a NoSQL database for storing job metadata and tracking.
     * Uses the default database specified in google-services.json
     * 
     * @return Singleton FirebaseFirestore instance
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
}
