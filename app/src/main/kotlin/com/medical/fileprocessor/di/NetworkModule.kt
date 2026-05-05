package com.medical.fileprocessor.di

import com.google.firebase.auth.FirebaseAuth
import com.medical.fileprocessor.network.ApiService
import com.medical.fileprocessor.network.FirebaseAuthInterceptor
import com.medical.fileprocessor.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for Network dependencies.
 * Provides singleton instances of Retrofit and OkHttpClient.
 *
 * Supports three backend environments (configured via [Constants.ACTIVE_BACKEND_ENV]):
 *  - EMULATOR    → http://10.0.2.2:8000/ (AVD → host machine)
 *  - LOCAL_DEVICE → http://<LAN-IP>:8000/ (physical device on same Wi-Fi)
 *  - CLOUD        → Firebase Cloud Functions URL
 *
 * Interceptor chain (in order):
 * 1. FirebaseAuthInterceptor - adds authentication token
 * 2. HttpLoggingInterceptor  - logs request/response (debug only)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides HTTP logging interceptor.
     *
     * In debug: Logs full request/response bodies
     * In release: Logging is disabled for performance
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (com.medical.fileprocessor.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provides Firebase authentication interceptor.
     *
     * Adds Firebase ID token to every request so the backend can
     * verify the caller's identity.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthInterceptor(
        firebaseAuth: FirebaseAuth,
    ): FirebaseAuthInterceptor {
        return FirebaseAuthInterceptor(firebaseAuth)
    }

    /**
     * Provides OkHttpClient with custom configuration.
     *
     * Configuration:
     * - Firebase auth interceptor (adds token)
     * - Logging interceptor (debug only)
     * - Extended timeouts for local CPU inference (60 s read/write)
     * - Connection pooling for efficiency
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        firebaseAuthInterceptor: FirebaseAuthInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(firebaseAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            // Increased for local CPU inference which can take 5–15 s per image
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectionPool(okhttp3.ConnectionPool(5, 5, TimeUnit.MINUTES))
            .build()
    }

    /**
     * Provides Retrofit instance.
     *
     * Base URL is resolved from [Constants.resolveBaseUrl] so it automatically
     * picks the correct host for emulator, physical device, or cloud.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.resolveBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    /**
     * Provides Retrofit API Service.
     *
     * Every call automatically includes the Firebase auth token via the
     * [FirebaseAuthInterceptor].
     */
    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit,
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
