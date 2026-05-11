package com.medical.fileprocessor.model;

/**
 * Processing status values
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lcom/medical/fileprocessor/model/ProcessingStatus;", "", "(Ljava/lang/String;I)V", "UPLOADING", "QUEUED", "PROCESSING", "COMPLETED", "FAILED", "app_release"})
public enum ProcessingStatus {
    /*public static final*/ UPLOADING /* = new UPLOADING() */,
    /*public static final*/ QUEUED /* = new QUEUED() */,
    /*public static final*/ PROCESSING /* = new PROCESSING() */,
    /*public static final*/ COMPLETED /* = new COMPLETED() */,
    /*public static final*/ FAILED /* = new FAILED() */;
    
    ProcessingStatus() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.medical.fileprocessor.model.ProcessingStatus> getEntries() {
        return null;
    }
}