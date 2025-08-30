package com.finalworksystem.application.use_case.auth

import com.finalworksystem.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class LogoutUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<Result<Boolean>> {
        return authRepository.logout()
    }
}
