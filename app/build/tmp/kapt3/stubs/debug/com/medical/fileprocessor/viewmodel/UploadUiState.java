package com.medical.fileprocessor.viewmodel;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.ViewModel;
import com.medical.fileprocessor.network.ProcessRequest;
import com.medical.fileprocessor.network.ProcessResponse;
import com.medical.fileprocessor.repository.AuthRepository;
import com.medical.fileprocessor.repository.ProcessRepository;
import com.medical.fileprocessor.repository.StorageRepository;
import com.medical.fileprocessor.util.Constants;
import com.medical.fileprocessor.util.Resource;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * UI State for the Upload Screen
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0010\b\u0002\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\tJ\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0011\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007H\u00c6\u0003J3\u0010\u0013\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0010\b\u0002\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0019\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001a"}, d2 = {"Lcom/medical/fileprocessor/viewmodel/UploadUiState;", "", "selectedFileUri", "Landroid/net/Uri;", "fileName", "", "status", "Lcom/medical/fileprocessor/util/Resource;", "Lcom/medical/fileprocessor/network/ProcessResponse;", "(Landroid/net/Uri;Ljava/lang/String;Lcom/medical/fileprocessor/util/Resource;)V", "getFileName", "()Ljava/lang/String;", "getSelectedFileUri", "()Landroid/net/Uri;", "getStatus", "()Lcom/medical/fileprocessor/util/Resource;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class UploadUiState {
    @org.jetbrains.annotations.Nullable()
    private final android.net.Uri selectedFileUri = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String fileName = null;
    @org.jetbrains.annotations.Nullable()
    private final com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse> status = null;
    
    public UploadUiState(@org.jetbrains.annotations.Nullable()
    android.net.Uri selectedFileUri, @org.jetbrains.annotations.Nullable()
    java.lang.String fileName, @org.jetbrains.annotations.Nullable()
    com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse> status) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.net.Uri getSelectedFileUri() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getFileName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse> getStatus() {
        return null;
    }
    
    public UploadUiState() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.net.Uri component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.medical.fileprocessor.viewmodel.UploadUiState copy(@org.jetbrains.annotations.Nullable()
    android.net.Uri selectedFileUri, @org.jetbrains.annotations.Nullable()
    java.lang.String fileName, @org.jetbrains.annotations.Nullable()
    com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse> status) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}