package com.finalworksystem.infrastructure.api

import com.finalworksystem.infrastructure.api.interceptor.ApiHeadersInterceptor
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber

/**
 * Handles `401 Unauthorized` responses of the server.
 *
 * The API has no refresh token, so the session cannot be renewed: the user is
 * logged out and the original response is returned to the caller (`null`).
 * A failed login (wrong credentials) does not end the session.
 */
class SessionExpiredAuthenticator(
    private val onSessionExpired: suspend () -> Unit
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (!ApiHeadersInterceptor.isLoginRequest(response.request)) {
            Timber.i("Session expired: %s", response.request.url.encodedPath)
            runBlocking { onSessionExpired() }
        }

        return null
    }
}
