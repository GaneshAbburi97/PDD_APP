package com.medical.fileprocessor.di;

import com.amplifyframework.auth.AuthCategory;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageCategory;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

/**
 * Hilt module for AWS/Amplify dependencies.
 *
 * Provides access to Amplify categories (Auth, Storage) to facilitate 
 * dependency injection and improved testability in repositories.
 */
@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\r\u0010\u0003\u001a\u00020\u0004H\u0007\u00a2\u0006\u0002\u0010\u0005J\r\u0010\u0006\u001a\u00020\u0004H\u0007\u00a2\u0006\u0002\u0010\u0005\u00a8\u0006\u0007"}, d2 = {"Lcom/medical/fileprocessor/di/AwsModule;", "", "()V", "provideAmplifyAuth", "error/NonExistentClass", "()Lerror/NonExistentClass;", "provideAmplifyStorage", "app_release"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class AwsModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.medical.fileprocessor.di.AwsModule INSTANCE = null;
    
    private AwsModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final AuthCategory provideAmplifyAuth() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final StorageCategory provideAmplifyStorage() {
        return null;
    }
}