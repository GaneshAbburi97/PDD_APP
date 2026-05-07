package com.medical.fileprocessor.network;

import com.google.firebase.auth.FirebaseAuth;
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
public final class FirebaseAuthInterceptor_Factory implements Factory<FirebaseAuthInterceptor> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public FirebaseAuthInterceptor_Factory(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public FirebaseAuthInterceptor get() {
    return newInstance(firebaseAuthProvider.get());
  }

  public static FirebaseAuthInterceptor_Factory create(
      Provider<FirebaseAuth> firebaseAuthProvider) {
    return new FirebaseAuthInterceptor_Factory(firebaseAuthProvider);
  }

  public static FirebaseAuthInterceptor newInstance(FirebaseAuth firebaseAuth) {
    return new FirebaseAuthInterceptor(firebaseAuth);
  }
}
