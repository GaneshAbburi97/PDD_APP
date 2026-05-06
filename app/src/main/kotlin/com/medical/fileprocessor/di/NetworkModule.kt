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
 * Interceptor chain (in order):
 * 1. FirebaseAuthInterceptor - adds authentication token
 * 2. HttpLoggingInterceptor - logs request/response (debug only)
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
     * Adds Firebase ID token to every request.
     * This authenticates the request to the backend.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthInterceptor(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthInterceptor {
        return FirebaseAuthInterceptor(firebaseAuth)
    }

    /**
     * Provides OkHttpClient with custom configuration.
     * 
     * Configuration:
     * - Firebase auth interceptor (adds token)
     * - Logging interceptor (debug only)
     * - Retry interceptor (handles transient failures)
     * - 60 second timeouts for research-mode processing
     * - Connection pooling for efficiency
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        firebaseAuthInterceptor: FirebaseAuthInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Add Firebase auth first (most important)
            .addInterceptor(firebaseAuthInterceptor)
            // Add retry interceptor for research stability
            .addInterceptor { chain ->
                var request = chain.request()
                var response = chain.proceed(request)
                var tryCount = 0
                val maxLimit = 3

                while (!response.isSuccessful && tryCount < maxLimit) {
                    tryCount++
                    Thread.sleep(2000L * tryCount) // Exponential backoff
                    response.close()
                    response = chain.proceed(request)
                }
                response
            }
            // Add logging last (to see final request with auth)
            .addInterceptor(loggingInterceptor)
            // Increased timeouts for heavy AI processing
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            // Connection pooling
            .connectionPool(okhttp3.ConnectionPool(5, 5, TimeUnit.MINUTES))
            .build()
    }

    /**
     * Provides Retrofit instance.
     * 
     * Base URL: Configurable via Constants (PROD/EMULATOR/DEVICE)
     * Converter: Gson for JSON serialization
     * Client: OkHttpClient with auth + retry
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    /**
     * Provides Retrofit API Service.
     *
     * This is used to make API calls to the backend.
     * Every call automatically includes Firebase auth token.
     */
    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit,
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
