package com.medical.fileprocessor.di;

import com.google.firebase.auth.FirebaseAuth;
import com.medical.fileprocessor.network.FirebaseAuthInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NetworkModule_ProvideFirebaseAuthInterceptorFactory implements Factory<FirebaseAuthInterceptor> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public NetworkModule_ProvideFirebaseAuthInterceptorFactory(
      Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public FirebaseAuthInterceptor get() {
    return provideFirebaseAuthInterceptor(firebaseAuthProvider.get());
  }

  public static NetworkModule_ProvideFirebaseAuthInterceptorFactory create(
      Provider<FirebaseAuth> firebaseAuthProvider) {
    return new NetworkModule_ProvideFirebaseAuthInterceptorFactory(firebaseAuthProvider);
  }

  public static FirebaseAuthInterceptor provideFirebaseAuthInterceptor(FirebaseAuth firebaseAuth) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFirebaseAuthInterceptor(firebaseAuth));
  }
}
