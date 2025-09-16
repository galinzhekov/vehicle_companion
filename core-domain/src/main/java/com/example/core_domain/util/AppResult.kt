package com.example.core_domain.util

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val throwable: Throwable) : AppResult<Nothing>()
}

suspend fun <T> safeCall(
    block: suspend () -> T,
    success: (T) -> Unit,
    error: (Throwable) -> Unit
) {
    try {
        val result = block()
        success(result)
    } catch (e: Throwable) {
        error(e)
    }
}
