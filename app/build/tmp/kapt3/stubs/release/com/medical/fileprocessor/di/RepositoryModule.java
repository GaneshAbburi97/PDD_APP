package com.medical.fileprocessor.di;

import com.medical.fileprocessor.repository.*;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

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
@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\'J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\'\u00a8\u0006\u000f"}, d2 = {"Lcom/medical/fileprocessor/di/RepositoryModule;", "", "()V", "bindAuthRepository", "Lcom/medical/fileprocessor/repository/AuthRepository;", "firebaseAuthRepository", "Lcom/medical/fileprocessor/repository/FirebaseAuthRepository;", "bindProcessRepository", "Lcom/medical/fileprocessor/repository/ProcessRepository;", "processRepositoryImpl", "Lcom/medical/fileprocessor/repository/ProcessRepositoryImpl;", "bindStorageRepository", "Lcom/medical/fileprocessor/repository/StorageRepository;", "firebaseStorageRepository", "Lcom/medical/fileprocessor/repository/FirebaseStorageRepository;", "app_release"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class RepositoryModule {
    
    public RepositoryModule() {
        super();
    }
    
    /**
     * Binds FirebaseAuthRepository to AuthRepository interface.
     *
     * When code injects AuthRepository, it gets FirebaseAuthRepository.
     * This allows easy swapping for testing (mock implementation).
     */
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.medical.fileprocessor.repository.AuthRepository bindAuthRepository(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.FirebaseAuthRepository firebaseAuthRepository);
    
    /**
     * Binds ProcessRepositoryImpl to ProcessRepository interface.
     *
     * Handles medical file processing through REST API calls.
     */
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.medical.fileprocessor.repository.ProcessRepository bindProcessRepository(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.ProcessRepositoryImpl processRepositoryImpl);
    
    /**
     * Binds FirebaseStorageRepository to StorageRepository interface.
     *
     * MIGRATION POINT: Changed from AmplifyStorageRepository to FirebaseStorageRepository
     *
     * When code injects StorageRepository, it gets FirebaseStorageRepository.
     * This replaces AWS S3 storage with Firebase Storage.
     */
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.medical.fileprocessor.repository.StorageRepository bindStorageRepository(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.FirebaseStorageRepository firebaseStorageRepository);
}