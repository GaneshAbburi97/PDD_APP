package com.medical.fileprocessor.repository;

import android.content.Context;
import android.net.Uri;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageException;
import com.amplifyframework.storage.options.StorageUploadFileOptions;
import com.medical.fileprocessor.util.Resource;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * AWS Amplify implementation of StorageRepository
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002J$\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\n2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/medical/fileprocessor/repository/AmplifyStorageRepository;", "Lcom/medical/fileprocessor/repository/StorageRepository;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getFileFromUri", "Ljava/io/File;", "uri", "Landroid/net/Uri;", "uploadFile", "Lkotlinx/coroutines/flow/Flow;", "Lcom/medical/fileprocessor/util/Resource;", "", "fileName", "app_release"})
public final class AmplifyStorageRepository implements com.medical.fileprocessor.repository.StorageRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    @javax.inject.Inject()
    public AmplifyStorageRepository(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<java.lang.String>> uploadFile(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String fileName) {
        return null;
    }
    
    private final java.io.File getFileFromUri(android.net.Uri uri) {
        return null;
    }
}