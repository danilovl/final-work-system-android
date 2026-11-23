package com.finalworksystem.infrastructure.image

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.finalworksystem.infrastructure.api.ApiConstant
import com.finalworksystem.infrastructure.api.ApiEndpoint
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.Path.Companion.toOkioPath

/**
 * Profile images are loaded by Coil (memory + disk cache, downsampling, crossfade)
 * through the authenticated image HTTP client. This class builds their URLs and
 * drops cached images when they may be outdated.
 */
class ProfileImageCache(
    private val imageLoader: ImageLoader,
    globalCacheManager: GlobalCacheManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Images are private data of the signed-in user; they do not survive the session.
        scope.launch {
            globalCacheManager.cacheCleanupEvents.collect { clear() }
        }
    }

    /** Forces the next request of the user's image to go to the server. */
    fun invalidate(userId: Int) {
        val url = url(userId)
        imageLoader.memoryCache?.let { memoryCache ->
            memoryCache.keys.filter { it.key == url }.forEach { memoryCache.remove(it) }
        }
        imageLoader.diskCache?.remove(url)
    }

    fun clear() {
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
    }

    companion object {
        private const val DISK_CACHE_DIRECTORY = "image_cache"
        private const val DISK_CACHE_SIZE = 20L * 1024 * 1024

        fun url(userId: Int): String {
            val path = ApiEndpoint.User.GET_PROFILE_IMAGE.replace("{id}", userId.toString())
            return ApiConstant.BASE_URL.toHttpUrl().resolve(path).toString()
        }

        fun createImageLoader(context: Context, callFactory: () -> Call.Factory): ImageLoader {
            return ImageLoader.Builder(context)
                .components {
                    add(OkHttpNetworkFetcherFactory(callFactory = callFactory))
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve(DISK_CACHE_DIRECTORY).toOkioPath())
                        .maxSizeBytes(DISK_CACHE_SIZE)
                        .build()
                }
                .crossfade(true)
                .build()
        }
    }
}
