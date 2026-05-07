package com.medical.fileprocessor.model;

import java.io.Serializable;

/**
 * Enum representing the possible states of a processing job.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b\u00a8\u0006\f"}, d2 = {"Lcom/medical/fileprocessor/model/ProcessingStatus;", "", "displayName", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getDisplayName", "()Ljava/lang/String;", "PENDING", "UPLOADING", "PROCESSING", "COMPLETED", "FAILED", "app_release"})
public enum ProcessingStatus {
    /*public static final*/ PENDING /* = new PENDING(null) */,
    /*public static final*/ UPLOADING /* = new UPLOADING(null) */,
    /*public static final*/ PROCESSING /* = new PROCESSING(null) */,
    /*public static final*/ COMPLETED /* = new COMPLETED(null) */,
    /*public static final*/ FAILED /* = new FAILED(null) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayName = null;
    
    ProcessingStatus(java.lang.String displayName) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.medical.fileprocessor.model.ProcessingStatus> getEntries() {
        return null;
    }
}