package com.finalworksystem.infrastructure.api

import android.util.Log
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.session.LogoutReason
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

class AuthInterceptor(
    private val userService: UserService,
    private val globalCacheManager: GlobalCacheManager,
    private val isCacheEnabled: Boolean
) : Interceptor {

    private data class CacheEntry(
        val response: Response,
        val timestamp: Long,
        val body: ByteArray
    )

    private val cache = ConcurrentHashMap<String, CacheEntry>()
    private val cacheTimeoutMs = 10_000L // 10 seconds
    private val maxRetries = 3
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            globalCacheManager.cacheCleanupEvents.collect { cacheCleanupEvent ->
                Log.d("AuthInterceptor", "Cache cleanup event received: ${cacheCleanupEvent.reason}")
                clearCache()
            }
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val isLoginRequest = originalRequest.url.encodedPath.contains(ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN)

        val requestBuilder = originalRequest.newBuilder()
            .addHeader(ApiConstant.AUTH_KEY, ApiConstant.API_KEY_VALUE)

        if (!isLoginRequest) {
            val token = runBlocking { userService.getTokenSafe() }
            val username = runBlocking { userService.getUserNameSafe() }

            if (token == null || username == null) {
                Log.e("AuthInterceptor", "Token or username is null for authenticated request. Url: ${originalRequest.url} Token: $token, Username: $username")

                runBlocking {
                    userService.logout(LogoutReason.TOKEN_EXPIRED)
                }
                clearCache()

                return chain.proceed(originalRequest.newBuilder()
                    .addHeader(ApiConstant.AUTH_KEY, ApiConstant.API_KEY_VALUE)
                    .build())
            }

            requestBuilder
                .addHeader(ApiConstant.AUTH_USER_TOKEN_KEY, token)
                .addHeader(ApiConstant.AUTH_USER_USERNAME, username)
        }

        val newRequest = requestBuilder.build()
        val apiServiceName = getApiServiceName(newRequest)

        if (!isLoginRequest && newRequest.method == "POST" && apiServiceName != null) {
            clearCacheByServiceName(apiServiceName)
            Log.d("AuthInterceptor", "Cleared cache for service: $apiServiceName due to POST request")
        }

        if (!isLoginRequest && newRequest.method == "GET" && isCacheEnabled) {
            Log.d("AuthInterceptor", "Caching is ENABLED - checking for cached response for GET request: ${newRequest.url}")

            val cacheKey = generateCacheKey(newRequest, runBlocking { userService.getUserNameSafe() }, apiServiceName)
            val cachedEntry = cache[cacheKey]

            if (cachedEntry != null && System.currentTimeMillis() - cachedEntry.timestamp < cacheTimeoutMs) {
                Log.d("AuthInterceptor", "Cache HIT - returning cached response for: ${newRequest.url}")

                return cachedEntry.response.newBuilder()
                    .body(cachedEntry.body.toResponseBody(cachedEntry.response.body?.contentType()))
                    .build()
            } else {
                Log.d("AuthInterceptor", "Cache MISS - no valid cached response found for: ${newRequest.url}")
            }
        } else if (!isLoginRequest && newRequest.method == "GET") {
            Log.d("AuthInterceptor", "Caching is DISABLED")
        }

        var lastException: Exception? = null
        var response: Response?

        for (attempt in 1..maxRetries) {
            try {
                response = chain.proceed(newRequest)

                if (response.code == 401 && !isLoginRequest) {
                    runBlocking {
                        userService.logout(LogoutReason.TOKEN_EXPIRED)
                    }
                    clearCache()

                    return response
                }

                if (!isLoginRequest && newRequest.method == "GET" && response.isSuccessful && isCacheEnabled) {
                    Log.d("AuthInterceptor", "Caching is ENABLED - storing successful GET response in cache for: ${newRequest.url}")

                    val cacheKey = generateCacheKey(newRequest, runBlocking { userService.getUserNameSafe() }, apiServiceName)
                    val responseBody = response.body?.bytes()

                    if (responseBody != null) {
                        val cacheEntry = CacheEntry(
                            response = response,
                            timestamp = System.currentTimeMillis(),
                            body = responseBody
                        )
                        cache[cacheKey] = cacheEntry
                        Log.d("AuthInterceptor", "Response CACHED successfully for: ${newRequest.url}")

                        val newResponse = response.newBuilder()
                            .body(responseBody.toResponseBody(response.body?.contentType()))
                            .build()

                        return newResponse
                    } else {
                        Log.w("AuthInterceptor", "Cannot cache response - response body is null for: ${newRequest.url}")
                    }
                } else if (!isLoginRequest && newRequest.method == "GET" && response.isSuccessful && !isCacheEnabled) {
                    Log.d("AuthInterceptor", "Caching is DISABLED - not storing response in cache for: ${newRequest.url}")
                }

                return response

            } catch (e: Exception) {
                lastException = e
                val shouldRetry = when (e) {
                    is IOException, is SocketTimeoutException, is UnknownHostException -> true
                    else -> false
                }

                if (shouldRetry && attempt < maxRetries) {
                    Log.w("AuthInterceptor", "Request failed (attempt $attempt/$maxRetries): ${e.message}. Retrying...")
                    Thread.sleep(1000L * attempt)

                    continue
                } else {
                    Log.e("AuthInterceptor", "Request failed after $attempt attempts: ${e.message}")

                    throw e
                }
            }
        }

        throw lastException ?: IOException("Request failed after $maxRetries attempts")
    }

    private fun generateCacheKey(request: okhttp3.Request, username: String?, apiServiceName: String?): String {
        val url = request.url
        val baseUrl = "${url.scheme}://${url.host}${url.encodedPath}"
        
        val sortedQueryParams = url.queryParameterNames.sorted().joinToString("&") { paramName ->
            val values = url.queryParameterValues(paramName)
            values.mapIndexed { index, value ->
                if (values.size > 1) "$paramName[$index]=$value" else "$paramName=$value"
            }.joinToString("&")
        }

        val fullUrl = if (sortedQueryParams.isNotEmpty()) {
            "$baseUrl?$sortedQueryParams"
        } else {
            baseUrl
        }
        
        val keyData = "${apiServiceName ?: "unknown"}:${request.method}:$fullUrl:${username ?: "anonymous"}"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(keyData.toByteArray())
        
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun getApiServiceName(request: okhttp3.Request): String? {
        return request.header("X-Api-Service")
    }


    private fun clearCacheByServiceName(serviceName: String) {
        val keysToRemove = cache.keys.filter { it.startsWith("$serviceName:") }
        keysToRemove.forEach { cache.remove(it) }
        Log.d("AuthInterceptor", "Cleared ${keysToRemove.size} cache entries for service: $serviceName")
    }

    private fun clearCache() {
        cache.clear()
    }
}
