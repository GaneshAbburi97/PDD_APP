package com.medical.fileprocessor.repository;

import com.google.firebase.auth.FirebaseUser;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H&\u00a8\u0006\u0004"}, d2 = {"Lcom/medical/fileprocessor/repository/AuthRepository;", "", "getCurrentUser", "Lcom/google/firebase/auth/FirebaseUser;", "app_release"})
public abstract interface AuthRepository {
    
    @org.jetbrains.annotations.Nullable()
    public abstract com.google.firebase.auth.FirebaseUser getCurrentUser();
}