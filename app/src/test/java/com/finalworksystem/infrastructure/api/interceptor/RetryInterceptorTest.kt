package com.finalworksystem.infrastructure.api.interceptor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class RetryInterceptorTest {

    private val interceptor = RetryInterceptor(maxAttempts = 3, sleep = {})

    private fun chainFor(request: Request, failures: Int): Interceptor.Chain {
        var calls = 0
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.call().isCanceled() } returns false
        every { chain.proceed(any()) } answers {
            calls++
            if (calls <= failures) throw IOException("timeout")
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }
        return chain
    }

    @Test
    fun `GET is retried after a network error`() {
        val chain = chainFor(Request.Builder().url("http://localhost/api").build(), failures = 2)

        val response = interceptor.intercept(chain)

        assertEquals(200, response.code)
        verify(exactly = 3) { chain.proceed(any()) }
    }

    @Test(expected = IOException::class)
    fun `GET fails after the last attempt`() {
        interceptor.intercept(chainFor(Request.Builder().url("http://localhost/api").build(), failures = 3))
    }

    @Test
    fun `POST is never retried`() {
        val request = Request.Builder().url("http://localhost/api").post("{}".toRequestBody()).build()
        val chain = chainFor(request, failures = 1)

        runCatching { interceptor.intercept(chain) }

        verify(exactly = 1) { chain.proceed(any()) }
    }
}
