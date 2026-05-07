package com.medical.fileprocessor.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AwsAuthRepository_Factory implements Factory<AwsAuthRepository> {
  @Override
  public AwsAuthRepository get() {
    return newInstance();
  }

  public static AwsAuthRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AwsAuthRepository newInstance() {
    return new AwsAuthRepository();
  }

  private static final class InstanceHolder {
    private static final AwsAuthRepository_Factory INSTANCE = new AwsAuthRepository_Factory();
  }
}
