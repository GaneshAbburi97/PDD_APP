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
 * UI State for the Upload Screen
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0017\b\u0087\b\u0018\u00002\u00020\u0001BM\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0010\b\u0002\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\f\u00a2\u0006\u0002\u0010\u000eJ\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0011\u0010\u001a\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\nH\u00c6\u0003J\t\u0010\u001c\u001a\u00020\fH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\fH\u00c6\u0003JQ\u0010\u001e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0010\b\u0002\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020\f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\nH\u00d6\u0001J\t\u0010\"\u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0011R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0019\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006#"}, d2 = {"Lcom/medical/fileprocessor/viewmodel/UploadUiState;", "", "selectedFileUri", "Landroid/net/Uri;", "fileName", "", "status", "Lcom/medical/fileprocessor/util/Resource;", "Lcom/medical/fileprocessor/network/ProcessResponse;", "uploadProgress", "", "isNetworkAvailable", "", "isBackendOnline", "(Landroid/net/Uri;Ljava/lang/String;Lcom/medical/fileprocessor/util/Resource;IZZ)V", "getFileName", "()Ljava/lang/String;", "()Z", "getSelectedFileUri", "()Landroid/net/Uri;", "getStatus", "()Lcom/medical/fileprocessor/util/Resource;", "getUploadProgress", "()I", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class UploadUiState {
    @org.jetbrains.annotations.Nullable()
    private final android.net.Uri selectedFileUri = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String fileName = null;
    @org.jetbrains.annotations.Nullable()
    private final com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse> status = null;
    private final int uploadProgress = 0;
    private final boolean isNetworkAvailable = false;
    private final boolean isBackendOnline = false;
    
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
    
    public final int component4() {
        return 0;
    }
    
    public final boolean component5() {
        return false;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.medical.fileprocessor.viewmodel.UploadUiState copy(@org.jetbrains.annotations.Nullable()
    android.net.Uri selectedFileUri, @org.jetbrains.annotations.Nullable()
    java.lang.String fileName, @org.jetbrains.annotations.Nullable()
    com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse> status, int uploadProgress, boolean isNetworkAvailable, boolean isBackendOnline) {
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
    
    public UploadUiState(@org.jetbrains.annotations.Nullable()
    android.net.Uri selectedFileUri, @org.jetbrains.annotations.Nullable()
    java.lang.String fileName, @org.jetbrains.annotations.Nullable()
    com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.network.ProcessResponse> status, int uploadProgress, boolean isNetworkAvailable, boolean isBackendOnline) {
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
    
    public final int getUploadProgress() {
        return 0;
    }
    
    public final boolean isNetworkAvailable() {
        return false;
    }
    
    public final boolean isBackendOnline() {
        return false;
    }
    
    public UploadUiState() {
        super();
    }
}