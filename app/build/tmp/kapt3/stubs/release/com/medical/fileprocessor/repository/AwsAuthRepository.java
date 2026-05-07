package com.medical.fileprocessor.repository;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.medical.fileprocessor.model.User;
import com.medical.fileprocessor.util.Resource;
import kotlinx.coroutines.channels.ProducerScope;
import kotlinx.coroutines.flow.Flow;
import timber.log.Timber;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * AWS Cognito implementation of [AuthRepository] using Amplify.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006H\u0002J\n\u0010\t\u001a\u0004\u0018\u00010\bH\u0016J$\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0016J\b\u0010\u000f\u001a\u00020\u0004H\u0016J,\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\rH\u0016\u00a8\u0006\u0012"}, d2 = {"Lcom/medical/fileprocessor/repository/AwsAuthRepository;", "Lcom/medical/fileprocessor/repository/AuthRepository;", "()V", "fetchCurrentUserAttributes", "", "scope", "Lkotlinx/coroutines/channels/ProducerScope;", "Lcom/medical/fileprocessor/util/Resource;", "Lcom/medical/fileprocessor/model/User;", "getCurrentUser", "login", "Lkotlinx/coroutines/flow/Flow;", "email", "", "password", "logout", "register", "displayName", "app_release"})
public final class AwsAuthRepository implements com.medical.fileprocessor.repository.AuthRepository {
    
    @javax.inject.Inject()
    public AwsAuthRepository() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.User>> login(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.User>> register(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName) {
        return null;
    }
    
    @java.lang.Override()
    public void logout() {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.medical.fileprocessor.model.User getCurrentUser() {
        return null;
    }
    
    private final void fetchCurrentUserAttributes(kotlinx.coroutines.channels.ProducerScope<? super com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.User>> scope) {
    }
}