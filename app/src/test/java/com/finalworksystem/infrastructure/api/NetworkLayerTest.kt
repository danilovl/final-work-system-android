package com.finalworksystem.infrastructure.api

import com.finalworksystem.infrastructure.api.interceptor.ApiHeadersInterceptor
import com.finalworksystem.infrastructure.api.interceptor.CacheInvalidationInterceptor
import com.finalworksystem.infrastructure.api.interceptor.RetryInterceptor
import com.finalworksystem.infrastructure.api.interceptor.ShortTermCacheInterceptor
import com.finalworksystem.infrastructure.session.AuthData
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.util.concurrent.TimeUnit

/**
 * Exercises the interceptor chain of [ApiClient] against a real HTTP server.
 */
class NetworkLayerTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var server: MockWebServer
    private lateinit var cache: Cache

    private var authData: AuthData? = AuthData(token = "test-token", username = "test-user")
    private var missingCredentialsCalls = 0
    private var sessionExpiredCalls = 0

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        cache = Cache(temporaryFolder.newFolder("http_cache"), 1024 * 1024)
    }

    @After
    fun tearDown() {
        server.close()
        cache.close()
    }

    private fun client(withCache: Boolean = false): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(
                ApiHeadersInterceptor(
                    credentials = { authData },
                    onMissingCredentials = { missingCredentialsCalls++ }
                )
            )
            .addInterceptor(CacheInvalidationInterceptor(cache))
            .addInterceptor(RetryInterceptor(retryDelayMs = 0))
            .authenticator(SessionExpiredAuthenticator { sessionExpiredCalls++ })

        if (withCache) {
            builder.cache(cache).addNetworkInterceptor(ShortTermCacheInterceptor(maxAgeSeconds = 60))
        }

        return builder.build()
    }

    private fun get(path: String) = Request.Builder().url(server.url(path)).get().build()

    private fun post(path: String) = Request.Builder().url(server.url(path)).post("{}".toRequestBody()).build()

    private fun OkHttpClient.execute(request: Request): Int = newCall(request).execute().use { it.code }

    @Test
    fun `authenticated request carries api key and user credentials`() {
        server.enqueue(MockResponse(code = 200))

        client().execute(get("/api/key/users/detail"))

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals(ApiConstant.API_KEY_VALUE, recorded.headers[ApiConstant.AUTH_KEY])
        assertEquals("test-token", recorded.headers[ApiConstant.AUTH_USER_TOKEN_KEY])
        assertEquals("test-user", recorded.headers[ApiConstant.AUTH_USER_USERNAME])
    }

    @Test
    fun `login request carries only the api key`() {
        authData = null
        server.enqueue(MockResponse(code = 200))

        client().execute(post(ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN))

        val recorded = server.takeRequest(1, TimeUnit.SECONDS)!!
        assertEquals(ApiConstant.API_KEY_VALUE, recorded.headers[ApiConstant.AUTH_KEY])
        assertNull(recorded.headers[ApiConstant.AUTH_USER_TOKEN_KEY])
        assertEquals(0, missingCredentialsCalls)
    }

    @Test
    fun `missing credentials return local 401 without contacting the server`() {
        authData = null

        val code = client().execute(get("/api/key/works/author"))

        assertEquals(401, code)
        assertEquals(0, server.requestCount)
        assertEquals(1, missingCredentialsCalls)
    }

    @Test
    fun `401 of the server ends the session`() {
        server.enqueue(MockResponse(code = 401))

        val code = client().execute(get("/api/key/works/author"))

        assertEquals(401, code)
        assertEquals(1, sessionExpiredCalls)
    }

    @Test
    fun `failed login does not end the session`() {
        server.enqueue(MockResponse(code = 401))

        client().execute(post(ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN))

        assertEquals(0, sessionExpiredCalls)
    }

    @Test
    fun `repeated GET is served from the HTTP cache`() {
        server.enqueue(MockResponse(code = 200, body = "first"))
        val client = client(withCache = true)

        client.execute(get("/api/key/conversations"))
        client.execute(get("/api/key/conversations"))

        assertEquals(1, server.requestCount)
    }

    @Test
    fun `modifying request clears the HTTP cache`() {
        server.enqueue(MockResponse(code = 200, body = "first"))
        server.enqueue(MockResponse(code = 200))
        server.enqueue(MockResponse(code = 200, body = "second"))
        val client = client(withCache = true)

        client.execute(get("/api/key/conversations"))
        client.execute(post("/api/key/conversations/1/messages"))
        client.execute(get("/api/key/conversations"))

        assertEquals(3, server.requestCount)
    }
}
