package com.medical.fileprocessor.viewmodel;

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

  public ResultViewModel_Factory(Provider<ProcessRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ResultViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ResultViewModel_Factory create(Provider<ProcessRepository> repositoryProvider) {
    return new ResultViewModel_Factory(repositoryProvider);
  }

  public static ResultViewModel newInstance(ProcessRepository repository) {
    return new ResultViewModel(repository);
  }
}
