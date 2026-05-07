package com.medical.fileprocessor.util;

/**
 * Generic sealed class for handling async data states.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002:\u0003\u0013\u0014\u0015B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0003J\u000e\u0010\f\u001a\n\u0018\u00010\rj\u0004\u0018\u0001`\u000eJ&\u0010\u000f\u001a\b\u0012\u0004\u0012\u0002H\u00100\u0000\"\u0004\b\u0001\u0010\u00102\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u0002H\u00100\u0012R\u0013\u0010\u0004\u001a\u0004\u0018\u00018\u00008F\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\tR\u0011\u0010\n\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\tR\u0011\u0010\u000b\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\t\u0082\u0001\u0003\u0016\u0017\u0018\u00a8\u0006\u0019"}, d2 = {"Lcom/medical/fileprocessor/util/Resource;", "T", "", "()V", "dataOrNull", "getDataOrNull", "()Ljava/lang/Object;", "isError", "", "()Z", "isLoading", "isSuccess", "exceptionOrNull", "Ljava/lang/Exception;", "Lkotlin/Exception;", "map", "R", "transform", "Lkotlin/Function1;", "Error", "Loading", "Success", "Lcom/medical/fileprocessor/util/Resource$Error;", "Lcom/medical/fileprocessor/util/Resource$Loading;", "Lcom/medical/fileprocessor/util/Resource$Success;", "app_debug"})
public abstract class Resource<T extends java.lang.Object> {
    
    private Resource() {
        super();
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    public final boolean isSuccess() {
        return false;
    }
    
    public final boolean isError() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final T getDataOrNull() {
        return null;
    }
    
    /**
     * Get exception from error state (null if not error).
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Exception exceptionOrNull() {
        return null;
    }
    
    /**
     * Map data to new type.
     */
    @org.jetbrains.annotations.NotNull()
    public final <R extends java.lang.Object>com.medical.fileprocessor.util.Resource<R> map(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super T, ? extends R> transform) {
        return null;
    }
    
    /**
     * Error state - error occurred during fetch.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u0000*\u0004\b\u0001\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\'\u0012\n\u0010\u0003\u001a\u00060\u0004j\u0002`\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00018\u0001\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\r\u0010\u0011\u001a\u00060\u0004j\u0002`\u0005H\u00c6\u0003J\u0010\u0010\u0012\u001a\u0004\u0018\u00018\u0001H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0013\u001a\u00020\bH\u00c6\u0003J8\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00010\u00002\f\b\u0002\u0010\u0003\u001a\u00060\u0004j\u0002`\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00018\u00012\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001\u00a2\u0006\u0002\u0010\u0015J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001J\t\u0010\u001c\u001a\u00020\bH\u00d6\u0001R\u0015\u0010\u0006\u001a\u0004\u0018\u00018\u0001\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0015\u0010\u0003\u001a\u00060\u0004j\u0002`\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001d"}, d2 = {"Lcom/medical/fileprocessor/util/Resource$Error;", "T", "Lcom/medical/fileprocessor/util/Resource;", "exception", "Ljava/lang/Exception;", "Lkotlin/Exception;", "data", "message", "", "(Ljava/lang/Exception;Ljava/lang/Object;Ljava/lang/String;)V", "getData", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getException", "()Ljava/lang/Exception;", "getMessage", "()Ljava/lang/String;", "component1", "component2", "component3", "copy", "(Ljava/lang/Exception;Ljava/lang/Object;Ljava/lang/String;)Lcom/medical/fileprocessor/util/Resource$Error;", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
    public static final class Error<T extends java.lang.Object> extends com.medical.fileprocessor.util.Resource<T> {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.Exception exception = null;
        @org.jetbrains.annotations.Nullable()
        private final T data = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String message = null;
        
        public Error(@org.jetbrains.annotations.NotNull()
        java.lang.Exception exception, @org.jetbrains.annotations.Nullable()
        T data, @org.jetbrains.annotations.NotNull()
        java.lang.String message) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.Exception getException() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final T getData() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.Exception component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final T component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.medical.fileprocessor.util.Resource.Error<T> copy(@org.jetbrains.annotations.NotNull()
        java.lang.Exception exception, @org.jetbrains.annotations.Nullable()
        T data, @org.jetbrains.annotations.NotNull()
        java.lang.String message) {
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
    
    /**
     * Loading state - data is being fetched.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u0000*\u0004\b\u0001\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0011\u0012\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00018\u0001\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\b\u001a\u0004\u0018\u00018\u0001H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0006J \u0010\t\u001a\b\u0012\u0004\u0012\u00028\u00010\u00002\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00018\u0001H\u00c6\u0001\u00a2\u0006\u0002\u0010\nJ\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0015\u0010\u0003\u001a\u0004\u0018\u00018\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0013"}, d2 = {"Lcom/medical/fileprocessor/util/Resource$Loading;", "T", "Lcom/medical/fileprocessor/util/Resource;", "data", "(Ljava/lang/Object;)V", "getData", "()Ljava/lang/Object;", "Ljava/lang/Object;", "component1", "copy", "(Ljava/lang/Object;)Lcom/medical/fileprocessor/util/Resource$Loading;", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class Loading<T extends java.lang.Object> extends com.medical.fileprocessor.util.Resource<T> {
        @org.jetbrains.annotations.Nullable()
        private final T data = null;
        
        public Loading(@org.jetbrains.annotations.Nullable()
        T data) {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final T getData() {
            return null;
        }
        
        public Loading() {
        }
        
        @org.jetbrains.annotations.Nullable()
        public final T component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.medical.fileprocessor.util.Resource.Loading<T> copy(@org.jetbrains.annotations.Nullable()
        T data) {
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
    
    /**
     * Success state - data fetched successfully.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u0000*\u0004\b\u0001\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\r\u0012\u0006\u0010\u0003\u001a\u00028\u0001\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\b\u001a\u00028\u0001H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\t\u001a\b\u0012\u0004\u0012\u00028\u00010\u00002\b\b\u0002\u0010\u0003\u001a\u00028\u0001H\u00c6\u0001\u00a2\u0006\u0002\u0010\nJ\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u00d6\u0003J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001J\t\u0010\u0011\u001a\u00020\u0012H\u00d6\u0001R\u0013\u0010\u0003\u001a\u00028\u0001\u00a2\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0013"}, d2 = {"Lcom/medical/fileprocessor/util/Resource$Success;", "T", "Lcom/medical/fileprocessor/util/Resource;", "data", "(Ljava/lang/Object;)V", "getData", "()Ljava/lang/Object;", "Ljava/lang/Object;", "component1", "copy", "(Ljava/lang/Object;)Lcom/medical/fileprocessor/util/Resource$Success;", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class Success<T extends java.lang.Object> extends com.medical.fileprocessor.util.Resource<T> {
        private final T data = null;
        
        public Success(T data) {
        }
        
        public final T getData() {
            return null;
        }
        
        public final T component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.medical.fileprocessor.util.Resource.Success<T> copy(T data) {
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
}