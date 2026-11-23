package com.finalworksystem.infrastructure.api.interceptor

import com.finalworksystem.infrastructure.api.ApiConstant
import com.finalworksystem.infrastructure.session.AuthData
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber

/**
 * Adds the API key to every request and the user credentials to every request
 * except the login request.
 *
 * When the credentials are missing the request is not sent to the server at all,
 * a local `401` is returned instead and [onMissingCredentials] is invoked.
 */
class ApiHeadersInterceptor(
    private val credentials: suspend () -> AuthData?,
    private val onMissingCredentials: (suspend () -> Unit)? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
            .header(ApiConstant.AUTH_KEY, ApiConstant.API_KEY_VALUE)

        if (!isLoginRequest(request)) {
            val authData = runBlocking { credentials() }
            if (authData == null) {
                Timber.w("Missing auth data for authenticated request: %s", request.url.encodedPath)
                onMissingCredentials?.let { runBlocking { it() } }

                return unauthorizedResponse(request)
            }

            builder
                .header(ApiConstant.AUTH_USER_TOKEN_KEY, authData.token)
                .header(ApiConstant.AUTH_USER_USERNAME, authData.username)
        }

        return chain.proceed(builder.build())
    }

    private fun unauthorizedResponse(request: Request): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized: missing session")
            .body(ByteArray(0).toResponseBody(null))
            .build()
    }

    companion object {
        fun isLoginRequest(request: Request): Boolean {
            return request.url.encodedPath.contains(ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN)
        }
    }
}
