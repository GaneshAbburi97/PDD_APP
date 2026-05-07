package com.medical.fileprocessor.repository;

import com.google.firebase.firestore.FirebaseFirestore;
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
public final class FirestoreJobRepository_Factory implements Factory<FirestoreJobRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  public FirestoreJobRepository_Factory(Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public FirestoreJobRepository get() {
    return newInstance(firestoreProvider.get());
  }

  public static FirestoreJobRepository_Factory create(
      Provider<FirebaseFirestore> firestoreProvider) {
    return new FirestoreJobRepository_Factory(firestoreProvider);
  }

  public static FirestoreJobRepository newInstance(FirebaseFirestore firestore) {
    return new FirestoreJobRepository(firestore);
  }
}
