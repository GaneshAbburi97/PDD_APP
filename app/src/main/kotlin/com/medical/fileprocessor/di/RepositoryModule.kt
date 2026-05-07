package com.medical.fileprocessor.di

import com.medical.fileprocessor.repository.AuthRepository
import com.medical.fileprocessor.repository.AuthRepositoryImpl
import com.medical.fileprocessor.repository.ProcessRepository
import com.medical.fileprocessor.repository.ProcessRepositoryImpl
import com.medical.fileprocessor.repository.StorageRepository
import com.medical.fileprocessor.repository.FirebaseStorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProcessRepository(
        processRepositoryImpl: ProcessRepositoryImpl
    ): ProcessRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        firebaseStorageRepository: FirebaseStorageRepository
    ): StorageRepository
}
