package com.finalworksystem.domain.user.repository

import kotlinx.coroutines.flow.Flow

interface PutUserRepository {
    /** Fails with [com.finalworksystem.domain.user.model.PasswordChangeException] on a rejected change. */
    fun changePassword(currentPassword: String, newPassword: String): Flow<Result<Unit>>
}
