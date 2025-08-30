package com.finalworksystem.data.user.repository

import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.repository.SessionUserRepository
import com.finalworksystem.infrastructure.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SessionUserRepository(
    private val sessionManager: SessionManager
) : SessionUserRepository {

    override fun saveUser(user: User): Flow<Result<Boolean>> = safeFlowResult {
        withContext(Dispatchers.IO) {
            sessionManager.saveUser(user)
        }
        Result.success(true)
    }

    override fun updateUser(user: User): Flow<Result<Boolean>> = safeFlowResult {
        withContext(Dispatchers.IO) {
            sessionManager.saveUser(user)
        }
        Result.success(true)
    }

    override fun clearUser(): Flow<Result<Boolean>> = safeFlowResult {
        withContext(Dispatchers.IO) {
            sessionManager.clearSession()
        }
        Result.success(true)
    }
}