package com.medical.fileprocessor.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.medical.fileprocessor.network.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ProcessRepositoryImpl_Factory implements Factory<ProcessRepositoryImpl> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<FirebaseAuth> firebaseAuthProvider;

  private final Provider<FirestoreJobRepository> firestoreJobRepositoryProvider;

  public ProcessRepositoryImpl_Factory(Provider<ApiService> apiServiceProvider,
      Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<FirestoreJobRepository> firestoreJobRepositoryProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.firebaseAuthProvider = firebaseAuthProvider;
    this.firestoreJobRepositoryProvider = firestoreJobRepositoryProvider;
  }

  @Override
  public ProcessRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), firebaseAuthProvider.get(), firestoreJobRepositoryProvider.get());
  }

  public static ProcessRepositoryImpl_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<FirestoreJobRepository> firestoreJobRepositoryProvider) {
    return new ProcessRepositoryImpl_Factory(apiServiceProvider, firebaseAuthProvider, firestoreJobRepositoryProvider);
  }

  public static ProcessRepositoryImpl newInstance(ApiService apiService, FirebaseAuth firebaseAuth,
      FirestoreJobRepository firestoreJobRepository) {
    return new ProcessRepositoryImpl(apiService, firebaseAuth, firestoreJobRepository);
  }
}
