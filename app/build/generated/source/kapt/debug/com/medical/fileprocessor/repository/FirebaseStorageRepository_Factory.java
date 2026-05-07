package com.medical.fileprocessor.repository;

import android.content.Context;
import com.google.firebase.storage.FirebaseStorage;
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
public final class FirebaseStorageRepository_Factory implements Factory<FirebaseStorageRepository> {
  private final Provider<FirebaseStorage> firebaseStorageProvider;

  private final Provider<Context> contextProvider;

  public FirebaseStorageRepository_Factory(Provider<FirebaseStorage> firebaseStorageProvider,
      Provider<Context> contextProvider) {
    this.firebaseStorageProvider = firebaseStorageProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public FirebaseStorageRepository get() {
    return newInstance(firebaseStorageProvider.get(), contextProvider.get());
  }

  public static FirebaseStorageRepository_Factory create(
      Provider<FirebaseStorage> firebaseStorageProvider, Provider<Context> contextProvider) {
    return new FirebaseStorageRepository_Factory(firebaseStorageProvider, contextProvider);
  }

  public static FirebaseStorageRepository newInstance(FirebaseStorage firebaseStorage,
      Context context) {
    return new FirebaseStorageRepository(firebaseStorage, context);
  }
}
