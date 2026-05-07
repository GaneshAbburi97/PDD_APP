package com.medical.fileprocessor.viewmodel;

import androidx.lifecycle.ViewModel;
import com.medical.fileprocessor.model.ProcessingJob;
import com.medical.fileprocessor.model.ProcessingResult;
import com.medical.fileprocessor.repository.ProcessRepository;
import com.medical.fileprocessor.util.Resource;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * ViewModel for Viewing Results and Tracking Status.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\u000e\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014R\u001c\u0010\u0005\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001f\u0010\u000b\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00070\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001f\u0010\u000f\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u00070\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/medical/fileprocessor/viewmodel/ResultViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/medical/fileprocessor/repository/ProcessRepository;", "(Lcom/medical/fileprocessor/repository/ProcessRepository;)V", "_jobStatus", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/medical/fileprocessor/util/Resource;", "Lcom/medical/fileprocessor/model/ProcessingJob;", "_processingResult", "Lcom/medical/fileprocessor/model/ProcessingResult;", "jobStatus", "Lkotlinx/coroutines/flow/StateFlow;", "getJobStatus", "()Lkotlinx/coroutines/flow/StateFlow;", "processingResult", "getProcessingResult", "fetchJobStatus", "", "jobId", "", "fetchResult", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ResultViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.medical.fileprocessor.repository.ProcessRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.ProcessingJob>> _jobStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.ProcessingJob>> jobStatus = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.ProcessingResult>> _processingResult = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.ProcessingResult>> processingResult = null;
    
    @javax.inject.Inject()
    public ResultViewModel(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.ProcessRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.ProcessingJob>> getJobStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.ProcessingResult>> getProcessingResult() {
        return null;
    }
    
    public final void fetchJobStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String jobId) {
    }
    
    public final void fetchResult(@org.jetbrains.annotations.NotNull()
    java.lang.String jobId) {
    }
}