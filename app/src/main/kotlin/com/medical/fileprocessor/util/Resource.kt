package com.medical.fileprocessor.util

/**
 * Generic sealed class for handling async data states.
 */
sealed class Resource<T> {

    /**
     * Loading state - data is being fetched.
     */
    data class Loading<T>(val data: T? = null) : Resource<T>()

    /**
     * Success state - data fetched successfully.
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Error state - error occurred during fetch.
     */
    data class Error<T>(
        val exception: Exception,
        val data: T? = null,
        val message: String = exception.localizedMessage ?: "Unknown error",
    ) : Resource<T>()

    /**
     * Check if resource is in loading state.
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Check if resource is successful.
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Check if resource has error.
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Get data from any state (null if not available).
     */
    val dataOrNull: T?
        get() = when (this) {
            is Loading -> data
            is Success -> data
            is Error -> data
        }

    /**
     * Get exception from error state (null if not error).
     */
    fun exceptionOrNull(): Exception? {
        return when (this) {
            is Error -> exception
            else -> null
        }
    }

    /**
     * Map data to new type.
     */
    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Loading -> Loading(data?.let { transform(it) })
            is Success -> Success(transform(data))
            is Error -> Error(exception, data?.let { transform(it) }, message)
        }
    }
}
