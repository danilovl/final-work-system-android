package com.finalworksystem.infrastructure.api.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Retries idempotent requests (GET, HEAD) on network errors.
 *
 * Modifying requests are never retried: a timeout after the server already
 * processed the request would otherwise create duplicates.
 */
class RetryInterceptor(
    private val maxAttempts: Int = 3,
    private val retryDelayMs: Long = 1000L,
    private val sleep: (Long) -> Unit = Thread::sleep
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val attempts = if (isIdempotent(request)) maxAttempts else 1

        var attempt = 1
        while (true) {
            try {
                return chain.proceed(request)
            } catch (e: IOException) {
                if (attempt >= attempts || isCanceled(chain)) {
                    Timber.w(e, "Request failed after %d attempt(s): %s", attempt, request.url.encodedPath)
                    throw e
                }

                Timber.d("Request failed (attempt %d/%d), retrying: %s", attempt, attempts, e.message)
                sleep(retryDelayMs * attempt)
                attempt++
            }
        }
    }

    private fun isIdempotent(request: Request): Boolean {
        return request.method == "GET" || request.method == "HEAD"
    }

    private fun isCanceled(chain: Interceptor.Chain): Boolean {
        return runCatching { chain.call().isCanceled() }.getOrDefault(false)
    }
}
