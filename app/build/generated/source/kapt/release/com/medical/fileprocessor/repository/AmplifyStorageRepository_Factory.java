package com.medical.fileprocessor.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AmplifyStorageRepository_Factory implements Factory<AmplifyStorageRepository> {
  private final Provider<Context> contextProvider;

  public AmplifyStorageRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AmplifyStorageRepository get() {
    return newInstance(contextProvider.get());
  }

  public static AmplifyStorageRepository_Factory create(Provider<Context> contextProvider) {
    return new AmplifyStorageRepository_Factory(contextProvider);
  }

  public static AmplifyStorageRepository newInstance(Context context) {
    return new AmplifyStorageRepository(context);
  }
}
