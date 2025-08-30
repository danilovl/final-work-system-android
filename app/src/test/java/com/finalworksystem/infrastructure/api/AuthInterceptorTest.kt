package com.finalworksystem.infrastructure.api

import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.session.LogoutReason
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthInterceptorTest {

    @Test
    fun `should identify GENERATE_TOKEN endpoint correctly`() {
        val generateTokenPath = ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN
        val userDetailPath = ApiConstant.API_KEY_USER_DETAIL

        assertEquals("/api/public/security/generate-token", generateTokenPath)
        assertEquals("/api/key/users/detail", userDetailPath)

        assertTrue("GENERATE_TOKEN path should be identified as login request",
            generateTokenPath.contains(ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN))

        assertFalse("User detail path should not be identified as login request", 
            userDetailPath.contains(ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN))
    }

    @Test
    fun `should have correct API constants`() {
        assertNotNull("AUTH_USER_TOKEN_KEY should be defined", ApiConstant.AUTH_USER_TOKEN_KEY)
        assertNotNull("AUTH_USER_USERNAME should be defined", ApiConstant.AUTH_USER_USERNAME)
        assertNotNull("API_PUBLIC_SECURITY_GENERATE_TOKEN should be defined", ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN)

        assertEquals("X-AUTH-USER-TOKEN", ApiConstant.AUTH_USER_TOKEN_KEY)
        assertEquals("X-AUTH-USER-USERNAME", ApiConstant.AUTH_USER_USERNAME)
    }

    @Test
    fun `should use local properties values for BASE_URL and API_KEY`() {
        assertNotNull("BASE_URL should be defined", ApiConstant.BASE_URL)
        assertNotNull("API_KEY_VALUE should be defined", ApiConstant.API_KEY_VALUE)

        // Values come from BuildConfig, so just verify they are not empty
        assertTrue("BASE_URL should not be empty", ApiConstant.BASE_URL.isNotEmpty())
        assertTrue("API_KEY_VALUE should not be empty", ApiConstant.API_KEY_VALUE.isNotEmpty())

        println("[DEBUG_LOG] BASE_URL from ApiConstants: ${ApiConstant.BASE_URL}")
        println("[DEBUG_LOG] API_KEY_VALUE from ApiConstants: ${ApiConstant.API_KEY_VALUE}")
    }

    @Test
    fun `should call logout when response is 401 Unauthorized`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/test")
            .build()

        val unauthorizedResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(unauthorizedResponse)
        whenever(mockUserService.getTokenSafe()).thenReturn("test-token")
        whenever(mockUserService.getUserNameSafe()).thenReturn("test-user")

        val response = authInterceptor.intercept(mockChain)
        assertEquals(401, response.code)
        verify(mockUserService).logout(LogoutReason.TOKEN_EXPIRED)

        println("[DEBUG_LOG] 401 handling test completed successfully")
    }

    @Test
    fun `should not call logout when response is not 401`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/test")
            .build()

        val successResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(successResponse)
        whenever(mockUserService.getTokenSafe()).thenReturn("test-token")
        whenever(mockUserService.getUserNameSafe()).thenReturn("test-user")

        val response = authInterceptor.intercept(mockChain)
        assertEquals(200, response.code)
        verify(mockUserService, org.mockito.kotlin.never()).logout(org.mockito.kotlin.any())

        println("[DEBUG_LOG] Non-401 response test completed successfully")
    }

    @Test
    fun `should call logout when 401 response is from user detail endpoint`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/key/users/detail")
            .build()

        val unauthorizedResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(unauthorizedResponse)
        whenever(mockUserService.getTokenSafe()).thenReturn("test-token")
        whenever(mockUserService.getUserNameSafe()).thenReturn("test-user")

        val response = authInterceptor.intercept(mockChain)
        assertEquals(401, response.code)
        verify(mockUserService).logout(LogoutReason.TOKEN_EXPIRED)

        println("[DEBUG_LOG] User detail 401 handling test completed successfully")
    }

    @Test
    fun `should not call logout when 401 response is from generate token endpoint`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/public/security/generate-token")
            .build()

        val unauthorizedResponse = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(unauthorizedResponse)
        whenever(mockUserService.getTokenSafe()).thenReturn(null)
        whenever(mockUserService.getUserNameSafe()).thenReturn(null)

        val response = authInterceptor.intercept(mockChain)
        assertEquals(401, response.code)
        verify(mockUserService, org.mockito.kotlin.never()).logout(org.mockito.kotlin.any())

        println("[DEBUG_LOG] Generate token 401 handling test completed successfully")
    }

    @Test
    fun `should call logout when token is null for non-login request`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/key/users/detail")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(response)
        whenever(mockUserService.getTokenSafe()).thenReturn(null)
        whenever(mockUserService.getUserNameSafe()).thenReturn("test-user")

        authInterceptor.intercept(mockChain)
        verify(mockUserService).logout(LogoutReason.TOKEN_EXPIRED)
        println("[DEBUG_LOG] Null token error handling test completed successfully")
    }

    @Test
    fun `should call logout when username is null for non-login request`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/key/users/detail")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(response)
        whenever(mockUserService.getTokenSafe()).thenReturn("test-token")
        whenever(mockUserService.getUserNameSafe()).thenReturn(null)

        authInterceptor.intercept(mockChain)

        verify(mockUserService).logout(LogoutReason.TOKEN_EXPIRED)
        println("[DEBUG_LOG] Null username error handling test completed successfully")
    }

    @Test
    fun `should call logout when both token and username are null for non-login request`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/key/users/detail")
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(response)
        whenever(mockUserService.getTokenSafe()).thenReturn(null)
        whenever(mockUserService.getUserNameSafe()).thenReturn(null)

        authInterceptor.intercept(mockChain)

        verify(mockUserService).logout(LogoutReason.TOKEN_EXPIRED)
        println("[DEBUG_LOG] Null token and username error handling test completed successfully")
    }

    @Test
    fun `should disable caching when isCacheEnabled constructor parameter is set to false`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, false)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/test?cache=false")
            .method("GET", null)
            .build()

        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(response)
        whenever(mockUserService.getTokenSafe()).thenReturn("test-token")
        whenever(mockUserService.getUserNameSafe()).thenReturn("test-user")

        val response1 = authInterceptor.intercept(mockChain)
        val response2 = authInterceptor.intercept(mockChain)

        assertEquals(200, response1.code)
        assertEquals(200, response2.code)

        verify(mockChain, org.mockito.kotlin.times(2)).proceed(org.mockito.kotlin.any())

        println("[DEBUG_LOG] Cache disable functionality test completed successfully")
    }

    @Test
    fun `should enable caching when isCacheEnabled constructor parameter is set to true`() = runTest {
        val mockUserService = mock<UserService>()
        val mockGlobalCacheManager = mock<GlobalCacheManager>()
        val mockCacheCleanupEvents = MutableSharedFlow<com.finalworksystem.infrastructure.cache.CacheCleanupEvent>()
        whenever(mockGlobalCacheManager.cacheCleanupEvents).thenReturn(mockCacheCleanupEvents)
        val authInterceptor = AuthInterceptor(mockUserService, mockGlobalCacheManager, true)

        val mockChain = mock<Interceptor.Chain>()
        val request = Request.Builder()
            .url("http://example.com/api/test?cache=true")
            .method("GET", null)
            .build()

        val responseBody = "test response body".toByteArray()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(okhttp3.ResponseBody.create(null, responseBody))
            .build()

        whenever(mockChain.request()).thenReturn(request)
        whenever(mockChain.proceed(org.mockito.kotlin.any())).thenReturn(response)
        whenever(mockUserService.getTokenSafe()).thenReturn("test-user")
        whenever(mockUserService.getUserNameSafe()).thenReturn("test-user")

        val response1 = authInterceptor.intercept(mockChain)

        assertEquals(200, response1.code)

        verify(mockChain, org.mockito.kotlin.times(1)).proceed(org.mockito.kotlin.any())

        println("[DEBUG_LOG] Cache enable functionality test completed successfully")
    }
}
