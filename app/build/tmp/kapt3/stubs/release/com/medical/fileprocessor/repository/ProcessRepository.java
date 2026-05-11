package com.medical.fileprocessor.repository;

import com.medical.fileprocessor.model.ProcessingJob;
import com.medical.fileprocessor.model.ProcessingResult;
import com.medical.fileprocessor.network.*;
import com.medical.fileprocessor.util.Resource;
import kotlinx.coroutines.flow.Flow;
import okhttp3.MultipartBody;

/**
 * Interface defining operations for medical file processing.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u001c\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00040\u00032\u0006\u0010\b\u001a\u00020\tH&J\u001c\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\u00040\u00032\u0006\u0010\b\u001a\u00020\tH&J\u001c\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00040\u00032\u0006\u0010\u000e\u001a\u00020\u000fH&J\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\u00040\u00032\u0006\u0010\u0012\u001a\u00020\u0013H&\u00a8\u0006\u0014"}, d2 = {"Lcom/medical/fileprocessor/repository/ProcessRepository;", "", "checkBackendHealth", "Lkotlinx/coroutines/flow/Flow;", "Lcom/medical/fileprocessor/util/Resource;", "Lcom/medical/fileprocessor/network/HealthStatus;", "getJobStatus", "Lcom/medical/fileprocessor/model/ProcessingJob;", "jobId", "", "getProcessingResult", "Lcom/medical/fileprocessor/model/ProcessingResult;", "startProcessing", "Lcom/medical/fileprocessor/network/ProcessResponse;", "request", "Lcom/medical/fileprocessor/network/ProcessRequest;", "uploadFile", "Lcom/medical/fileprocessor/network/UploadResponse;", "file", "Lokhttp3/MultipartBody$Part;", "app_release"})
public abstract interface ProcessRepository {
    
    /**
     * Checks if the backend is online and responding.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.HealthStatus>> checkBackendHealth();
    
    /**
     * Uploads a file directly to the processing backend.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.UploadResponse>> uploadFile(@org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part file);
    
    /**
     * Starts the processing of a medical file already in cloud storage.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse>> startProcessing(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.network.ProcessRequest request);
    
    /**
     * Polls the current status of a processing job.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.ProcessingJob>> getJobStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String jobId);
    
    /**
     * Fetches the detailed results once a job is COMPLETED.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.ProcessingResult>> getProcessingResult(@org.jetbrains.annotations.NotNull()
    java.lang.String jobId);
}