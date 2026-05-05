package com.medical.fileprocessor.di

import com.medical.fileprocessor.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository interfaces to their implementations.
 * 
 * Using @Binds is more efficient than @Provides for interface binding 
 * as Hilt doesn't generate unnecessary code to call the provider method.
 * 
 * Repository pattern benefits:
 * - Abstraction of data sources (Firebase, REST API, Mock, etc.)
 * - Easy to swap implementations for testing
 * - Clean separation of concerns
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds FirebaseAuthRepository to AuthRepository interface.
     * 
     * When code injects AuthRepository, it gets FirebaseAuthRepository.
     * This allows easy swapping for testing (mock implementation).
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository,
    ): AuthRepository

    /**
     * Binds ProcessRepositoryImpl to ProcessRepository interface.
     * 
     * Handles medical file processing through REST API calls and Firestore tracking.
     * 
     * ProcessRepositoryImpl is a concrete class, so we use @Binds.
     * It depends on:
     * - ApiService (Retrofit)
     * - FirebaseAuth (Firebase)
     * - FirestoreJobRepository (Firestore)
     */
    @Binds
    @Singleton
    abstract fun bindProcessRepository(
        processRepositoryImpl: ProcessRepositoryImpl,
    ): ProcessRepository

    /**
     * Binds FirebaseStorageRepository to StorageRepository interface.
     * 
     * When code injects StorageRepository, it gets FirebaseStorageRepository.
     */
    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        firebaseStorageRepository: FirebaseStorageRepository,
    ): StorageRepository
}
