package com.medical.fileprocessor.viewmodel;

import android.content.Context;
import com.medical.fileprocessor.network.NetworkManager;
import com.medical.fileprocessor.repository.AuthRepository;
import com.medical.fileprocessor.repository.ProcessRepository;
import com.medical.fileprocessor.repository.StorageRepository;
import com.medical.fileprocessor.util.ImageCompressor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class UploadViewModel_Factory implements Factory<UploadViewModel> {
  private final Provider<StorageRepository> storageRepositoryProvider;

  private final Provider<ProcessRepository> processRepositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<NetworkManager> networkManagerProvider;

  private final Provider<ImageCompressor> imageCompressorProvider;

  private final Provider<Context> contextProvider;

  public UploadViewModel_Factory(Provider<StorageRepository> storageRepositoryProvider,
      Provider<ProcessRepository> processRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<NetworkManager> networkManagerProvider,
      Provider<ImageCompressor> imageCompressorProvider, Provider<Context> contextProvider) {
    this.storageRepositoryProvider = storageRepositoryProvider;
    this.processRepositoryProvider = processRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.networkManagerProvider = networkManagerProvider;
    this.imageCompressorProvider = imageCompressorProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public UploadViewModel get() {
    return newInstance(storageRepositoryProvider.get(), processRepositoryProvider.get(), authRepositoryProvider.get(), networkManagerProvider.get(), imageCompressorProvider.get(), contextProvider.get());
  }

  public static UploadViewModel_Factory create(
      Provider<StorageRepository> storageRepositoryProvider,
      Provider<ProcessRepository> processRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<NetworkManager> networkManagerProvider,
      Provider<ImageCompressor> imageCompressorProvider, Provider<Context> contextProvider) {
    return new UploadViewModel_Factory(storageRepositoryProvider, processRepositoryProvider, authRepositoryProvider, networkManagerProvider, imageCompressorProvider, contextProvider);
  }

  public static UploadViewModel newInstance(StorageRepository storageRepository,
      ProcessRepository processRepository, AuthRepository authRepository,
      NetworkManager networkManager, ImageCompressor imageCompressor, Context context) {
    return new UploadViewModel(storageRepository, processRepository, authRepository, networkManager, imageCompressor, context);
  }
}
