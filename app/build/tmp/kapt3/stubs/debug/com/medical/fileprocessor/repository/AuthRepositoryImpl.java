package com.medical.fileprocessor.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\n\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/medical/fileprocessor/repository/AuthRepositoryImpl;", "Lcom/medical/fileprocessor/repository/AuthRepository;", "firebaseAuth", "Lcom/google/firebase/auth/FirebaseAuth;", "(Lcom/google/firebase/auth/FirebaseAuth;)V", "getCurrentUser", "Lcom/google/firebase/auth/FirebaseUser;", "app_debug"})
public final class AuthRepositoryImpl implements com.medical.fileprocessor.repository.AuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.auth.FirebaseAuth firebaseAuth = null;
    
    @javax.inject.Inject()
    public AuthRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.google.firebase.auth.FirebaseAuth firebaseAuth) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.google.firebase.auth.FirebaseUser getCurrentUser() {
        return null;
    }
}