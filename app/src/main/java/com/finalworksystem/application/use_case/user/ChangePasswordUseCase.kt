package com.finalworksystem.application.use_case.user

import com.finalworksystem.domain.user.repository.PutUserRepository
import kotlinx.coroutines.flow.Flow

class ChangePasswordUseCase(private val userRepository: PutUserRepository) {
    operator fun invoke(currentPassword: String, newPassword: String): Flow<Result<Unit>> =
        userRepository.changePassword(currentPassword, newPassword)
}
