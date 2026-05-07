package com.medical.fileprocessor.di;

import com.google.firebase.auth.FirebaseAuth;
import com.medical.fileprocessor.network.ApiService;
import com.medical.fileprocessor.network.FirebaseAuthInterceptor;
import com.medical.fileprocessor.util.Constants;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;

/**
 * Hilt module for Network dependencies.
 * Provides singleton instances of Retrofit and OkHttpClient.
 *
 * Interceptor chain (in order):
 * 1. FirebaseAuthInterceptor - adds authentication token
 * 2. Retry interceptor - async exponential backoff (no blocking)
 * 3. HttpLoggingInterceptor - logs request/response (debug only)
 *
 * RELIABILITY FEATURES:
 * - Exponential backoff retry (2s, 4s, 8s)
 * - Timeout handling (60s for AI inference)
 * - Connection pooling for efficiency
 */
@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J\b\u0010\u000b\u001a\u00020\fH\u0007J\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\bH\u0007J\u0010\u0010\u0011\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u000eH\u0007\u00a8\u0006\u0013"}, d2 = {"Lcom/medical/fileprocessor/di/NetworkModule;", "", "()V", "provideApiService", "Lcom/medical/fileprocessor/network/ApiService;", "retrofit", "Lretrofit2/Retrofit;", "provideFirebaseAuthInterceptor", "Lcom/medical/fileprocessor/network/FirebaseAuthInterceptor;", "firebaseAuth", "Lcom/google/firebase/auth/FirebaseAuth;", "provideLoggingInterceptor", "Lokhttp3/logging/HttpLoggingInterceptor;", "provideOkHttpClient", "Lokhttp3/OkHttpClient;", "loggingInterceptor", "firebaseAuthInterceptor", "provideRetrofit", "okHttpClient", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class NetworkModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.medical.fileprocessor.di.NetworkModule INSTANCE = null;
    
    private NetworkModule() {
        super();
    }
    
    /**
     * Provides HTTP logging interceptor.
     *
     * In debug: Logs full request/response bodies
     * In release: Logging is disabled for performance
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.logging.HttpLoggingInterceptor provideLoggingInterceptor() {
        return null;
    }
    
    /**
     * Provides Firebase authentication interceptor.
     *
     * Adds Firebase ID token to every request.
     * This authenticates the request to the backend.
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.medical.fileprocessor.network.FirebaseAuthInterceptor provideFirebaseAuthInterceptor(@org.jetbrains.annotations.NotNull()
    com.google.firebase.auth.FirebaseAuth firebaseAuth) {
        return null;
    }
    
    /**
     * Provides OkHttpClient with custom configuration.
     *
     * Configuration:
     * - Firebase auth interceptor (adds token)
     * - Async retry interceptor with exponential backoff
     * - Logging interceptor (debug only)
     * - 60 second timeouts for research-mode processing
     * - Connection pooling for efficiency
     *
     * SAFETY:
     * - No blocking Thread.sleep (uses async backoff)
     * - Transient failures automatically retried
     * - Timeout prevents hanging connections
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient provideOkHttpClient(@org.jetbrains.annotations.NotNull()
    okhttp3.logging.HttpLoggingInterceptor loggingInterceptor, @org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.network.FirebaseAuthInterceptor firebaseAuthInterceptor) {
        return null;
    }
    
    /**
     * Provides Retrofit instance.
     *
     * Base URL: Configurable via Constants (PROD/EMULATOR/DEVICE)
     * Converter: Gson for JSON serialization
     * Client: OkHttpClient with auth + retry + logging
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit provideRetrofit(@org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient) {
        return null;
    }
    
    /**
     * Provides Retrofit API Service.
     *
     * This is used to make API calls to the backend.
     * Every call automatically includes Firebase auth token.
     * Failed calls automatically retry with exponential backoff.
     */
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.medical.fileprocessor.network.ApiService provideApiService(@org.jetbrains.annotations.NotNull()
    retrofit2.Retrofit retrofit) {
        return null;
    }
}