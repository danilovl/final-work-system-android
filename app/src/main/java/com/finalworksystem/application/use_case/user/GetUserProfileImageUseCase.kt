package com.finalworksystem.application.use_case.user

import com.finalworksystem.domain.user.model.UserProfileImage
import com.finalworksystem.domain.user.repository.GetUserRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileImageUseCase(private val userRepository: GetUserRepository) {
    operator fun invoke(userId: Int): Flow<Result<UserProfileImage>> {
        return userRepository.getUserProfileImage(userId)
    }
}
