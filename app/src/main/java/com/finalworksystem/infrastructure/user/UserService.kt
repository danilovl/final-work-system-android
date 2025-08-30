package com.finalworksystem.infrastructure.user

import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserRole
import com.finalworksystem.infrastructure.session.LogoutReason
import com.finalworksystem.infrastructure.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class UserService(private val sessionManager: SessionManager) {
    suspend fun getUser(): User {
        return sessionManager.userFlow.firstOrNull() ?: throw IllegalStateException("User is null")
    }

    suspend fun getTokenSafe(): String? {
        repeat(3) { attempt ->
            val token = sessionManager.authTokenFlow.firstOrNull()
            if (token != null) {
                return token
            }

            if (attempt < 2) {
                delay(50)
            }
        }

        return null
    }

    suspend fun getUserNameSafe(): String? {
        repeat(3) { attempt ->
            val username = sessionManager.authUsernameFlow.firstOrNull()
            if (username != null) {
                return username
            }

            if (attempt < 2) {
                delay(50)
            }
        }

        return null
    }

    fun getUserFlow(): Flow<User> {
        return sessionManager.userFlow.map { user -> 
            user ?: throw IllegalStateException("User is null")
        }
    }

    fun getUserFlowSafe(): Flow<User?> {
        return sessionManager.userFlow
    }

    fun isLoggedInFlow(): Flow<Boolean> {
        return sessionManager.isLoggedInFlow
    }

    suspend fun isStudent(): Boolean {
        return getUser().roles.contains(UserRole.STUDENT.value)
    }

    suspend fun isSupervisor(): Boolean {
        return getUser().roles.contains(UserRole.SUPERVISOR.value)
    }

    suspend fun logout(reason: LogoutReason = LogoutReason.TOKEN_EXPIRED) {
        sessionManager.clearSession(reason)
    }
}
