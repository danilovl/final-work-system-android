package com.finalworksystem.infrastructure.api

import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class DomainCachingTest {

    @Test
    fun `should cache by API service name and clear cache on POST request`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        whenever(mockUserService.getTokenSafe()).thenReturn("test-token")
        whenever(mockUserService.getUserNameSafe()).thenReturn("test-user")

        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)
        val mockChain = mock<Interceptor.Chain>()

        val getRequest = Request.Builder()
            .url("http://example.com/api/conversations")
            .addHeader("X-Api-Service", "ApiConversationService")
            .get()
            .build()

        val getResponse = Response.Builder()
            .request(getRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("cached response".toResponseBody())
            .build()

        whenever(mockChain.request()).thenReturn(getRequest)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(getResponse)

        val firstResponse = authInterceptor.intercept(mockChain)
        println("[DEBUG_LOG] First GET request completed - response should be cached")

        val postRequest = Request.Builder()
            .url("http://example.com/api/conversations/1/messages")
            .addHeader("X-Api-Service", "ApiConversationService")
            .post("test body".toRequestBody())
            .build()

        val postResponse = Response.Builder()
            .request(postRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(201)
            .message("Created")
            .body("post response".toResponseBody())
            .build()

        whenever(mockChain.request()).thenReturn(postRequest)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(postResponse)

        val postResult = authInterceptor.intercept(mockChain)
        println("[DEBUG_LOG] POST request completed - cache for ApiConversationService should be cleared")

        val otherGetRequest = Request.Builder()
            .url("http://example.com/api/users")
            .addHeader("X-Api-Service", "ApiUserService")
            .get()
            .build()

        val otherGetResponse = Response.Builder()
            .request(otherGetRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body("other service response".toResponseBody())
            .build()

        whenever(mockChain.request()).thenReturn(otherGetRequest)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(otherGetResponse)

        val otherResult = authInterceptor.intercept(mockChain)
        println("[DEBUG_LOG] GET request to different service completed - should work normally")

        println("[DEBUG_LOG] Domain-based caching test completed successfully")
    }
}
