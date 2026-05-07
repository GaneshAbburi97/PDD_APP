package com.medical.fileprocessor.di;

import com.medical.fileprocessor.repository.AuthRepository;
import com.medical.fileprocessor.repository.AuthRepositoryImpl;
import com.medical.fileprocessor.repository.ProcessRepository;
import com.medical.fileprocessor.repository.ProcessRepositoryImpl;
import com.medical.fileprocessor.repository.StorageRepository;
import com.medical.fileprocessor.repository.FirebaseStorageRepository;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\'J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\'\u00a8\u0006\u000f"}, d2 = {"Lcom/medical/fileprocessor/di/RepositoryModule;", "", "()V", "bindAuthRepository", "Lcom/medical/fileprocessor/repository/AuthRepository;", "authRepositoryImpl", "Lcom/medical/fileprocessor/repository/AuthRepositoryImpl;", "bindProcessRepository", "Lcom/medical/fileprocessor/repository/ProcessRepository;", "processRepositoryImpl", "Lcom/medical/fileprocessor/repository/ProcessRepositoryImpl;", "bindStorageRepository", "Lcom/medical/fileprocessor/repository/StorageRepository;", "firebaseStorageRepository", "Lcom/medical/fileprocessor/repository/FirebaseStorageRepository;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class RepositoryModule {
    
    public RepositoryModule() {
        super();
    }
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.medical.fileprocessor.repository.AuthRepository bindAuthRepository(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.AuthRepositoryImpl authRepositoryImpl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.medical.fileprocessor.repository.ProcessRepository bindProcessRepository(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.ProcessRepositoryImpl processRepositoryImpl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.medical.fileprocessor.repository.StorageRepository bindStorageRepository(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.FirebaseStorageRepository firebaseStorageRepository);
}