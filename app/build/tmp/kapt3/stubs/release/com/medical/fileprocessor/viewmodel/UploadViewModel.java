package com.medical.fileprocessor.viewmodel;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.ViewModel;
import com.medical.fileprocessor.network.NetworkManager;
import com.medical.fileprocessor.network.ProcessRequest;
import com.medical.fileprocessor.network.ProcessResponse;
import com.medical.fileprocessor.repository.AuthRepository;
import com.medical.fileprocessor.repository.ProcessRepository;
import com.medical.fileprocessor.repository.StorageRepository;
import com.medical.fileprocessor.util.Constants;
import com.medical.fileprocessor.util.ImageCompressor;
import com.medical.fileprocessor.util.Resource;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.StateFlow;
import timber.log.Timber;
import javax.inject.Inject;

/**
 * ViewModel for File Upload
 * Orchestrates: Local Pick -> Cloud Upload -> Backend Process
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B9\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\b\u0001\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\u0006\u0010\u0016\u001a\u00020\u0017J\b\u0010\u0018\u001a\u00020\u0017H\u0002J\b\u0010\u0019\u001a\u00020\u0017H\u0014J\u0016\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eJ\u0006\u0010\u001f\u001a\u00020\u0017J\u0016\u0010 \u001a\u00020\u00172\u0006\u0010!\u001a\u00020\"H\u0082@\u00a2\u0006\u0002\u0010#J\u0006\u0010$\u001a\u00020\u0017R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006%"}, d2 = {"Lcom/medical/fileprocessor/viewmodel/UploadViewModel;", "Landroidx/lifecycle/ViewModel;", "storageRepository", "Lcom/medical/fileprocessor/repository/StorageRepository;", "processRepository", "Lcom/medical/fileprocessor/repository/ProcessRepository;", "authRepository", "Lcom/medical/fileprocessor/repository/AuthRepository;", "networkManager", "Lcom/medical/fileprocessor/network/NetworkManager;", "imageCompressor", "Lcom/medical/fileprocessor/util/ImageCompressor;", "context", "Landroid/content/Context;", "(Lcom/medical/fileprocessor/repository/StorageRepository;Lcom/medical/fileprocessor/repository/ProcessRepository;Lcom/medical/fileprocessor/repository/AuthRepository;Lcom/medical/fileprocessor/network/NetworkManager;Lcom/medical/fileprocessor/util/ImageCompressor;Landroid/content/Context;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/medical/fileprocessor/viewmodel/UploadUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "checkBackendHealth", "", "observeNetwork", "onCleared", "onFileSelected", "uri", "Landroid/net/Uri;", "name", "", "resetState", "startBackendProcessing", "request", "Lcom/medical/fileprocessor/network/ProcessRequest;", "(Lcom/medical/fileprocessor/network/ProcessRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startUploadAndProcess", "app_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class UploadViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.medical.fileprocessor.repository.StorageRepository storageRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.medical.fileprocessor.repository.ProcessRepository processRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.medical.fileprocessor.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.medical.fileprocessor.network.NetworkManager networkManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.medical.fileprocessor.util.ImageCompressor imageCompressor = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.medical.fileprocessor.viewmodel.UploadUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.medical.fileprocessor.viewmodel.UploadUiState> uiState = null;
    
    @javax.inject.Inject()
    public UploadViewModel(@org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.StorageRepository storageRepository, @org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.ProcessRepository processRepository, @org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.repository.AuthRepository authRepository, @org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.network.NetworkManager networkManager, @org.jetbrains.annotations.NotNull()
    com.medical.fileprocessor.util.ImageCompressor imageCompressor, @dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.medical.fileprocessor.viewmodel.UploadUiState> getUiState() {
        return null;
    }
    
    private final void observeNetwork() {
    }
    
    public final void checkBackendHealth() {
    }
    
    public final void onFileSelected(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void startUploadAndProcess() {
    }
    
    private final java.lang.Object startBackendProcessing(com.medical.fileprocessor.network.ProcessRequest request, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void resetState() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
}