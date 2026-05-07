package com.medical.fileprocessor.network;

import com.medical.fileprocessor.model.ProcessingJob;
import com.medical.fileprocessor.model.ProcessingResult;
import com.medical.fileprocessor.model.User;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.*;

/**
 * Retrofit API Service for medical file processing.
 *
 * Provides endpoints for authentication, file processing, and status tracking.
 * All functions are suspended for use with Kotlin Coroutines.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J$\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ$\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00040\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ$\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00040\u00032\b\b\u0001\u0010\r\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ$\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00040\u00032\b\b\u0001\u0010\r\u001a\u00020\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0012J$\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u00040\u00032\b\b\u0001\u0010\r\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016J$\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00040\u00032\b\b\u0001\u0010\u0019\u001a\u00020\u001aH\u00a7@\u00a2\u0006\u0002\u0010\u001b\u00a8\u0006\u001c"}, d2 = {"Lcom/medical/fileprocessor/network/ApiService;", "", "getJobStatus", "Lretrofit2/Response;", "Lcom/medical/fileprocessor/network/ApiResponse;", "Lcom/medical/fileprocessor/model/ProcessingJob;", "jobId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProcessingResult", "Lcom/medical/fileprocessor/model/ProcessingResult;", "login", "Lcom/medical/fileprocessor/model/User;", "request", "Lcom/medical/fileprocessor/network/LoginRequest;", "(Lcom/medical/fileprocessor/network/LoginRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "register", "Lcom/medical/fileprocessor/network/RegisterRequest;", "(Lcom/medical/fileprocessor/network/RegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startProcessing", "Lcom/medical/fileprocessor/network/ProcessResponse;", "Lcom/medical/fileprocessor/network/ProcessRequest;", "(Lcom/medical/fileprocessor/network/ProcessRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadFile", "Lcom/medical/fileprocessor/network/UploadResponse;", "file", "Lokhttp3/MultipartBody$Part;", "(Lokhttp3/MultipartBody$Part;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface ApiService {
    
    @retrofit2.http.POST(value = "auth/login")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.network.LoginRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.medical.fileprocessor.network.ApiResponse<com.medical.fileprocessor.model.User>>> $completion);
    
    @retrofit2.http.POST(value = "auth/register")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object register(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.network.RegisterRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.medical.fileprocessor.network.ApiResponse<com.medical.fileprocessor.model.User>>> $completion);
    
    /**
     * Option 1: Direct Multipart Upload to Backend
     */
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "upload")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object uploadFile(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part file, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.medical.fileprocessor.network.ApiResponse<com.medical.fileprocessor.network.UploadResponse>>> $completion);
    
    /**
     * Option 2: Notify backend to process a file already in cloud storage
     */
    @retrofit2.http.POST(value = "process")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object startProcessing(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.network.ProcessRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.medical.fileprocessor.network.ApiResponse<com.medical.fileprocessor.network.ProcessResponse>>> $completion);
    
    /**
     * Poll for the current status of a processing job
     */
    @retrofit2.http.GET(value = "process/status/{jobId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getJobStatus(@retrofit2.http.Path(value = "jobId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String jobId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.medical.fileprocessor.network.ApiResponse<com.medical.fileprocessor.model.ProcessingJob>>> $completion);
    
    /**
     * Retrieve the final detailed result of a processing job
     */
    @retrofit2.http.GET(value = "process/result/{jobId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getProcessingResult(@retrofit2.http.Path(value = "jobId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String jobId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.medical.fileprocessor.network.ApiResponse<com.medical.fileprocessor.model.ProcessingResult>>> $completion);
}