package com.medical.fileprocessor.di;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

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
@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u0007\u001a\u00020\bH\u0007\u00a8\u0006\t"}, d2 = {"Lcom/medical/fileprocessor/di/FirebaseModule;", "", "()V", "provideFirebaseAuth", "Lcom/google/firebase/auth/FirebaseAuth;", "provideFirebaseFirestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "provideFirebaseStorage", "Lcom/google/firebase/storage/FirebaseStorage;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class FirebaseModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.medical.fileprocessor.di.FirebaseModule INSTANCE = null;
    
    private FirebaseModule() {
        super();
    }
    
    /**
     * Provides Firebase Authentication instance.
     *
     * Firebase Auth handles user registration, login, and session management.
     *
     * @return Singleton FirebaseAuth instance
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.google.firebase.auth.FirebaseAuth provideFirebaseAuth() {
        return null;
    }
    
    /**
     * Provides Firebase Storage instance.
     *
     * Firebase Storage handles file uploads and downloads.
     * Configured with your project's storage bucket from google-services.json
     *
     * @return Singleton FirebaseStorage instance
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.google.firebase.storage.FirebaseStorage provideFirebaseStorage() {
        return null;
    }
    
    /**
     * Provides Firebase Firestore instance.
     *
     * Firestore is a NoSQL database for storing job metadata and tracking.
     * Uses the default database specified in google-services.json
     *
     * @return Singleton FirebaseFirestore instance
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.google.firebase.firestore.FirebaseFirestore provideFirebaseFirestore() {
        return null;
    }
}