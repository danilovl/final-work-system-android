package com.finalworksystem.application.use_case.user

import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.user.repository.GetUserRepository
import com.finalworksystem.infrastructure.cache.ProfileImageCacheManager
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RefreshUserDataOnStartupUseCase(
    private val userRepository: GetUserRepository,
    private val userService: UserService,
    private val profileImageCacheManager: ProfileImageCacheManager
) {
    operator fun invoke(): Flow<Result<Boolean>> = safeFlowResult {
        val hasUser = userService.isLoggedInFlow().first()

        if (!hasUser) {
            return@safeFlowResult Result.success(false)
        }

        val userDetailResult = userRepository.getUserDetail().first()

        if (userDetailResult.isSuccess) {
            val user = userDetailResult.getOrThrow()

            val profileImageResult = userRepository.getUserProfileImage(user.id).first()

            if (profileImageResult.isSuccess) {
                val profileImage = profileImageResult.getOrThrow()
                profileImageCacheManager.saveProfileImage(user.id, profileImage)
            }

            Result.success(true)
        } else {
            Result.failure(userDetailResult.exceptionOrNull() ?: Exception("Failed to get user details"))
        }
    }
}
