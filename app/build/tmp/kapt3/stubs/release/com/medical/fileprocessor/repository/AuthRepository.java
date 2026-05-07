package com.medical.fileprocessor.repository;

import com.medical.fileprocessor.model.User;
import com.medical.fileprocessor.util.Resource;
import kotlinx.coroutines.flow.Flow;

/**
 * Interface defining authentication operations.
 *
 * Using an interface allows for multiple implementations (e.g., Firebase, AWS, or Mock)
 * and facilitates unit testing.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H&J$\u0010\u0004\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u00060\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH&J\b\u0010\n\u001a\u00020\u000bH&J,\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u00060\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\bH&\u00a8\u0006\u000e"}, d2 = {"Lcom/medical/fileprocessor/repository/AuthRepository;", "", "getCurrentUser", "Lcom/medical/fileprocessor/model/User;", "login", "Lkotlinx/coroutines/flow/Flow;", "Lcom/medical/fileprocessor/util/Resource;", "email", "", "password", "logout", "", "register", "displayName", "app_release"})
public abstract interface AuthRepository {
    
    /**
     * Authenticates a user with email and password.
     * @return A Flow emitting [Resource] states containing the [User].
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.User>> login(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password);
    
    /**
     * Registers a new user account.
     * @return A Flow emitting [Resource] states containing the newly created [User].
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.medical.fileprocessor.util.Resource<com.medical.fileprocessor.model.User>> register(@org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName);
    
    /**
     * Logs the current user out of the system.
     */
    public abstract void logout();
    
    /**
     * Retrieves the currently authenticated user, if any.
     * @return The current [User] or null if no one is logged in.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract com.medical.fileprocessor.model.User getCurrentUser();
}