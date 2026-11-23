package com.finalworksystem.infrastructure.api.interceptor

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Network interceptor that lets OkHttp's HTTP cache keep successful GET responses
 * for a short time, so repeated requests of several screens hit the cache.
 *
 * Replaces the former hand-written in-memory cache: storage, expiration and
 * conditional requests are handled by [okhttp3.Cache].
 */
class ShortTermCacheInterceptor(private val maxAgeSeconds: Int) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (request.method != "GET" || !response.isSuccessful) {
            return response
        }

        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Expires")
            .header("Cache-Control", "private, max-age=$maxAgeSeconds")
            .build()
    }
}

/**
 * Application interceptor that drops cached responses after every modifying
 * request (POST, PUT, PATCH, DELETE), so lists never show data older than the change.
 */
class CacheInvalidationInterceptor(private val cache: Cache) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method == "GET" || request.method == "HEAD") {
            return chain.proceed(request)
        }

        try {
            return chain.proceed(request)
        } finally {
            evictAll(cache)
        }
    }

    companion object {
        fun evictAll(cache: Cache) {
            try {
                cache.evictAll()
            } catch (e: IOException) {
                Timber.w(e, "Failed to clear HTTP cache")
            }
        }
    }
}
