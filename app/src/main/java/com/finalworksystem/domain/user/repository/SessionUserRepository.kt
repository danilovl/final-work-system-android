package com.finalworksystem.domain.user.repository

import com.finalworksystem.domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface SessionUserRepository {
    fun saveUser(user: User): Flow<Result<Boolean>>
    fun updateUser(user: User): Flow<Result<Boolean>>
    fun clearUser(): Flow<Result<Boolean>>
}
