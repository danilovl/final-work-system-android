package com.finalworksystem.application.use_case.user

import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.user.repository.GetUserRepository
import com.finalworksystem.infrastructure.image.ProfileImageCache
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RefreshUserDataOnStartupUseCase(
    private val userRepository: GetUserRepository,
    private val userService: UserService,
    private val profileImageCache: ProfileImageCache
) {
    operator fun invoke(): Flow<Result<Boolean>> = safeFlowResult {
        val hasUser = userService.isLoggedInFlow().first()

        if (!hasUser) {
            return@safeFlowResult Result.success(false)
        }

        val userDetailResult = userRepository.getUserDetail().first()

        if (userDetailResult.isSuccess) {
            // The own profile image may have been changed on the web; reload it once per start.
            profileImageCache.invalidate(userDetailResult.getOrThrow().id)

            Result.success(true)
        } else {
            Result.failure(userDetailResult.exceptionOrNull() ?: Exception("Failed to get user details"))
        }
    }
}
