package com.medical.fileprocessor.viewmodel;

import android.content.Context;
import com.medical.fileprocessor.repository.AuthRepository;
import com.medical.fileprocessor.repository.ProcessRepository;
import com.medical.fileprocessor.repository.StorageRepository;
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

  private final Provider<Context> contextProvider;

  public UploadViewModel_Factory(Provider<StorageRepository> storageRepositoryProvider,
      Provider<ProcessRepository> processRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider, Provider<Context> contextProvider) {
    this.storageRepositoryProvider = storageRepositoryProvider;
    this.processRepositoryProvider = processRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public UploadViewModel get() {
    return newInstance(storageRepositoryProvider.get(), processRepositoryProvider.get(), authRepositoryProvider.get(), contextProvider.get());
  }

  public static UploadViewModel_Factory create(
      Provider<StorageRepository> storageRepositoryProvider,
      Provider<ProcessRepository> processRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider, Provider<Context> contextProvider) {
    return new UploadViewModel_Factory(storageRepositoryProvider, processRepositoryProvider, authRepositoryProvider, contextProvider);
  }

  public static UploadViewModel newInstance(StorageRepository storageRepository,
      ProcessRepository processRepository, AuthRepository authRepository, Context context) {
    return new UploadViewModel(storageRepository, processRepository, authRepository, context);
  }
}
