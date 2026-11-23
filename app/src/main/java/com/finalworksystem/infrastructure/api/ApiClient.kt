package com.finalworksystem.infrastructure.api

import android.content.Context
import com.finalworksystem.BuildConfig
import com.finalworksystem.infrastructure.api.interceptor.ApiHeadersInterceptor
import com.finalworksystem.infrastructure.api.interceptor.CacheInvalidationInterceptor
import com.finalworksystem.infrastructure.api.interceptor.RetryInterceptor
import com.finalworksystem.infrastructure.api.interceptor.ShortTermCacheInterceptor
import com.finalworksystem.infrastructure.api.serialization.ApiJson
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.session.LogoutReason
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Builds the HTTP clients of the application.
 *
 * - [httpClient] — REST API: credentials, retry of idempotent requests, short-term HTTP cache,
 *   logout on `401` via [SessionExpiredAuthenticator].
 * - [imageHttpClient] — images loaded by Coil: credentials only (Coil keeps its own caches).
 *
 * Both clients share one connection pool and dispatcher.
 */
class ApiClient(
    private val userService: UserService,
    context: Context,
    globalCacheManager: GlobalCacheManager
) {
    private val httpCache = Cache(File(context.cacheDir, "http_cache"), HTTP_CACHE_SIZE)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Responses of one user must never be served to the next one.
        scope.launch {
            globalCacheManager.cacheCleanupEvents.collect {
                CacheInvalidationInterceptor.evictAll(httpCache)
            }
        }
    }

    private val baseClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    val httpClient: OkHttpClient by lazy {
        val builder = baseClient.newBuilder()
            .addInterceptor(
                ApiHeadersInterceptor(
                    credentials = { userService.getAuthData() },
                    onMissingCredentials = { userService.logout(LogoutReason.TOKEN_EXPIRED) }
                )
            )
            .addInterceptor(CacheInvalidationInterceptor(httpCache))
            .addInterceptor(RetryInterceptor())
            .authenticator(SessionExpiredAuthenticator { userService.logout(LogoutReason.TOKEN_EXPIRED) })

        if (BuildConfig.API_CACHE_REQUEST) {
            builder
                .cache(httpCache)
                .addNetworkInterceptor(ShortTermCacheInterceptor(CACHE_MAX_AGE_SECONDS))
        }

        builder.addLogging().build()
    }

    val imageHttpClient: OkHttpClient by lazy {
        baseClient.newBuilder()
            // Images must not end the session; the avatar simply falls back to the placeholder.
            .addInterceptor(ApiHeadersInterceptor(credentials = { userService.getAuthData() }))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstant.BASE_URL)
            .client(httpClient)
            .addConverterFactory(ApiJson.asConverterFactory("application/json; charset=UTF-8".toMediaType()))
            .build()
    }

    val apiAuthService: ApiAuthService by lazy { retrofit.create() }
    val apiUserService: ApiUserService by lazy { retrofit.create() }
    val apiWorkService: ApiWorkService by lazy { retrofit.create() }
    val apiTaskService: ApiTaskService by lazy { retrofit.create() }
    val apiSystemEventService: ApiSystemEventService by lazy { retrofit.create() }
    val apiConversationService: ApiConversationService by lazy { retrofit.create() }
    val apiVersionService: ApiVersionService by lazy { retrofit.create() }
    val apiEventService: ApiEventService by lazy { retrofit.create() }

    private fun OkHttpClient.Builder.addLogging(): OkHttpClient.Builder {
        if (BuildConfig.DEBUG) {
            // Network logging only in debug builds; credentials are never written to logcat.
            addInterceptor(
                HttpLoggingInterceptor { message -> timber.log.Timber.tag("OkHttp").d(message) }.apply {
                    level = HttpLoggingInterceptor.Level.HEADERS
                    redactHeader(ApiConstant.AUTH_KEY)
                    redactHeader(ApiConstant.AUTH_USER_TOKEN_KEY)
                    redactHeader(ApiConstant.AUTH_USER_USERNAME)
                }
            )
        }

        return this
    }

    private companion object {
        const val TIMEOUT_SECONDS = 30L
        const val HTTP_CACHE_SIZE = 10L * 1024 * 1024
        const val CACHE_MAX_AGE_SECONDS = 10
    }
}
