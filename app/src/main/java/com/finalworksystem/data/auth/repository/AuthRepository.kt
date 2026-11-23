package com.finalworksystem.data.auth.repository

import com.finalworksystem.data.auth.model.LoginRequest
import com.finalworksystem.data.user.model.response.toDataModel
import com.finalworksystem.data.user.model.toDomainModel
import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.domain.auth.repository.AuthRepository
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.infrastructure.api.ApiAuthService
import com.finalworksystem.infrastructure.api.ApiUserService
import com.finalworksystem.infrastructure.session.AuthData
import com.finalworksystem.infrastructure.session.SessionManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AuthRepository(
    private val sessionManager: SessionManager,
    private val apiAuthService: ApiAuthService,
    private val apiUserService: ApiUserService
) : AuthRepository {

    override fun login(username: String, password: String): Flow<Result<String>> = safeFlowResult {
        val loginRequest = LoginRequest(username = username, password = password)

        val response = apiAuthService.login(loginRequest = loginRequest)

        val loginResult = response.handleResponse { loginResponse ->
            loginResponse.token
        }

        if (loginResult.isFailure) {
            return@safeFlowResult Result.failure(loginResult.exceptionOrNull() ?: Exception("Login failed"))
        }

        val token = loginResult.getOrThrow()

        val authData = AuthData(token = token, username = username)
        withContext(Dispatchers.IO) {
            sessionManager.saveAuthData(authData)
        }

        try {
            val userDetailResponse = apiUserService.getUserDetail()

            val userDetailResult = userDetailResponse.handleResponse { userDetail ->
                userDetail
            }

            if (userDetailResult.isFailure) {
                rollbackAuthData()
                return@safeFlowResult Result.failure(
                    userDetailResult.exceptionOrNull() ?: Exception("Failed to load user detail")
                )
            }

            val userDetail = userDetailResult.getOrThrow()
            val dataUser = userDetail.toDataModel()
            val domainUser = dataUser.toDomainModel().copy(token = token)

            withContext(Dispatchers.IO) {
                sessionManager.saveUser(domainUser)
            }

            Result.success(token)
        } catch (e: CancellationException) {
            rollbackAuthData()
            throw e
        } catch (e: Exception) {
            // Do not leave a half-created session (token without user) on disk.
            rollbackAuthData()
            Result.failure(Exception("Error processing login user detail: ${e.message}", e))
        }
    }

    private suspend fun rollbackAuthData() {
        withContext(NonCancellable + Dispatchers.IO) {
            sessionManager.clearAuthData()
        }
    }

    override fun logout(): Flow<Result<Boolean>> = safeFlowResult {
        withContext(Dispatchers.IO) {
            sessionManager.clearSession()
        }
        Result.success(true)
    }
}
