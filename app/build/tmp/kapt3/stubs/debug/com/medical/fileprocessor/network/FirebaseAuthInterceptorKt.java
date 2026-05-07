package com.medical.fileprocessor.network;

import com.google.firebase.auth.FirebaseAuth;
import okhttp3.Interceptor;
import okhttp3.Response;
import timber.log.Timber;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\f\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001f\u0010\u0000\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u0002H\u0002\u00a2\u0006\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"waitForResult", "T", "Lcom/google/android/gms/tasks/Task;", "(Lcom/google/android/gms/tasks/Task;)Ljava/lang/Object;", "app_debug"})
public final class FirebaseAuthInterceptorKt {
    
    /**
     * Extension function to wait for Firebase Task result synchronously.
     *
     * This is safe to call from OkHttp interceptor (not on main thread).
     *
     * @return The task result or null if failed
     */
    private static final <T extends java.lang.Object>T waitForResult(com.google.android.gms.tasks.Task<T> $this$waitForResult) {
        return null;
    }
}