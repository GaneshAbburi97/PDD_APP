package com.medical.fileprocessor.repository;

import android.net.Uri;
import com.medical.fileprocessor.util.Resource;
import kotlinx.coroutines.flow.Flow;

/**
 * Repository interface for cloud storage operations
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J$\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H&\u00a8\u0006\t"}, d2 = {"Lcom/medical/fileprocessor/repository/StorageRepository;", "", "uploadFile", "Lkotlinx/coroutines/flow/Flow;", "Lcom/medical/fileprocessor/util/Resource;", "", "uri", "Landroid/net/Uri;", "fileName", "app_release"})
public abstract interface StorageRepository {
    
    /**
     * Uploads a file to cloud storage
     *
     * @param uri Content URI of the file to upload
     * @param fileName Name to use for the uploaded file
     * @return Flow emitting Resource<String> with download URL on success
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<java.lang.String>> uploadFile(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String fileName);
}