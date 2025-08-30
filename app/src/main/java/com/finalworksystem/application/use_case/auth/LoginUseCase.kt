package com.finalworksystem.application.use_case.auth

import com.finalworksystem.domain.auth.model.LoginRequest
import com.finalworksystem.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class LoginUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(loginRequest: LoginRequest): Flow<Result<String>> {
        return authRepository.login(
            username = loginRequest.username,
            password = loginRequest.password
        )
    }
}
