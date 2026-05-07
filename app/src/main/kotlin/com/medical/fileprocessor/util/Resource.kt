package com.medical.fileprocessor.util

sealed class Resource<T>(
    open val data: T? = null,
    open val message: String? = null,
    open val exception: Throwable? = null
) {
    class Success<T>(override val data: T) : Resource<T>(data)
    class Error<T>(override val exception: Throwable? = null, override val message: String? = null, override val data: T? = null) : Resource<T>(data, message, exception)
    class Loading<T>(override val data: T? = null) : Resource<T>(data)
}

/**
 * Extension properties for cleaner resource handling
 */
val <T> Resource<T>.dataOrNull: T?
    get() = data

val <T> Resource<T>.isLoading: Boolean
    get() = this is Resource.Loading

val <T> Resource<T>.isSuccess: Boolean
    get() = this is Resource.Success

val <T> Resource<T>.isError: Boolean
    get() = this is Resource.Error
