package com.medical.fileprocessor.viewmodel;

import com.medical.fileprocessor.network.NetworkManager;
import com.medical.fileprocessor.repository.ProcessRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class ResultViewModel_Factory implements Factory<ResultViewModel> {
  private final Provider<ProcessRepository> repositoryProvider;

  private final Provider<NetworkManager> networkManagerProvider;

  public ResultViewModel_Factory(Provider<ProcessRepository> repositoryProvider,
      Provider<NetworkManager> networkManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.networkManagerProvider = networkManagerProvider;
  }

  @Override
  public ResultViewModel get() {
    return newInstance(repositoryProvider.get(), networkManagerProvider.get());
  }

  public static ResultViewModel_Factory create(Provider<ProcessRepository> repositoryProvider,
      Provider<NetworkManager> networkManagerProvider) {
    return new ResultViewModel_Factory(repositoryProvider, networkManagerProvider);
  }

  public static ResultViewModel newInstance(ProcessRepository repository,
      NetworkManager networkManager) {
    return new ResultViewModel(repository, networkManager);
  }
}
