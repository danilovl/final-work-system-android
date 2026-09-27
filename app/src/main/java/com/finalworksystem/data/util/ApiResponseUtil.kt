package com.finalworksystem.data.util

import retrofit2.Response

class UnauthorizedException(message: String) : Exception(message)

class NotFoundException(message: String = "Resource not found") : Exception(message)

fun Throwable?.isNotFound(): Boolean {
    if (this == null) {
        return false
    }

    if (this is NotFoundException) {
        return true
    }

    val msg = message?.lowercase() ?: ""
    return msg.contains("404") || msg.contains("not found")
}

object ApiResponseUtil {
    fun <T, R> handleApiResponse(
        response: Response<T>,
        transform: (T) -> R,
        notFoundMessage: String = "Resource not found",
        accessDeniedMessage: String = "Access denied",
        operationName: String = "operation"
    ): Result<R> {
        return when (response.code()) {
            200 -> {
                val body = response.body()
                if (body != null) {
                    Result.success(transform(body))
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            }
            204 -> Result.failure(Exception("Unexpected content for no-content response"))
            401 -> Result.failure(UnauthorizedException("Invalid credentials"))
            404 -> Result.failure(NotFoundException(notFoundMessage))
            403 -> Result.failure(Exception(accessDeniedMessage))
            else -> {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(transform(body))
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("Failed to perform $operationName: ${response.code()} ${response.message()}"))
                }
            }
        }
    }

    fun handleApiResponse(
        response: Response<*>,
        notFoundMessage: String = "Resource not found",
        accessDeniedMessage: String = "Access denied",
        operationName: String = "operation"
    ): Result<Unit> {
        return when (response.code()) {
            200, 204 -> Result.success(Unit)
            401 -> Result.failure(UnauthorizedException("Invalid credentials"))
            404 -> Result.failure(NotFoundException(notFoundMessage))
            403 -> Result.failure(Exception(accessDeniedMessage))
            else -> {
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to perform $operationName: ${response.code()} ${response.message()}"))
                }
            }
        }
    }

    fun <T, R> handleApiResponseWithNullable(
        response: Response<T>,
        transform: (T) -> R
    ): Result<R?> {
        return when (response.code()) {
            404 -> Result.success(null)
            401 -> Result.failure(UnauthorizedException("Invalid credentials"))
            200 -> {
                val body = response.body()
                if (body != null) {
                    Result.success(transform(body))
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            }
            else -> {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(transform(body))
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
                }
            }
        }
    }

}

fun <T, R> Response<T>.handleResponse(transform: (T) -> R): Result<R> {
    return ApiResponseUtil.handleApiResponse(this, transform)
}

fun <T> Response<T>.handleResponse(): Result<T> {
    return ApiResponseUtil.handleApiResponse(this, transform = { it })
}

fun Response<*>.handleSimpleResponse(): Result<Unit> {
    return ApiResponseUtil.handleApiResponse(this)
}

fun <T> Response<T>.handleResponseWithNullable(): Result<T?> {
    return ApiResponseUtil.handleApiResponseWithNullable(this) { it }
}

