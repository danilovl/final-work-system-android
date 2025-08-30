package com.finalworksystem.domain.auth.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(username: String, password: String): Flow<Result<String>>

    fun logout(): Flow<Result<Boolean>>
}
