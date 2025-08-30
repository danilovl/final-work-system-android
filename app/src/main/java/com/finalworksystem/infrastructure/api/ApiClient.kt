package com.finalworksystem.infrastructure.api

import android.content.Context
import com.finalworksystem.BuildConfig
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.user.UserService
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class ApiClient(
    private val userService: UserService, 
    private val context: Context,
    private val globalCacheManager: GlobalCacheManager
) {
    private val timeout = 30L
    private val cacheSize = 10 * 1024 * 1024L // 10 MB

    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(cacheDir, cacheSize)

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(AuthInterceptor(userService, globalCacheManager, BuildConfig.API_CACHE_REQUEST))
            .addInterceptor(getCacheInterceptor())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()
    }

    private fun getCacheInterceptor() = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)

        if (request.method == "GET") {
            val cacheControl = CacheControl.Builder()
                .maxAge(10, TimeUnit.SECONDS)
                .build()

            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        } else {
            response
        }
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstant.BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiAuthService: ApiAuthService by lazy {
        retrofit.create(ApiAuthService::class.java)
    }

    val apiUserService: ApiUserService by lazy {
        retrofit.create(ApiUserService::class.java)
    }

    val apiWorkService: ApiWorkService by lazy {
        retrofit.create(ApiWorkService::class.java)
    }

    val apiTaskService: ApiTaskService by lazy {
        retrofit.create(ApiTaskService::class.java)
    }

    val apiSystemEventService: ApiSystemEventService by lazy {
        retrofit.create(ApiSystemEventService::class.java)
    }

    val apiConversationService: ApiConversationService by lazy {
        retrofit.create(ApiConversationService::class.java)
    }

    val apiVersionService: ApiVersionService by lazy {
        retrofit.create(ApiVersionService::class.java)
    }

    val apiEventService: ApiEventService by lazy {
        retrofit.create(ApiEventService::class.java)
    }
}
