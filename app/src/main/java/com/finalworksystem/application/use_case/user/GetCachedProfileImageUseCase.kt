package com.finalworksystem.application.use_case.user

import com.finalworksystem.domain.user.model.UserProfileImage
import com.finalworksystem.infrastructure.cache.ProfileImageCacheManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetCachedProfileImageUseCase(
    private val profileImageCacheManager: ProfileImageCacheManager,
    private val getUserProfileImageUseCase: GetUserProfileImageUseCase
) {
    operator fun invoke(userId: Int): Flow<UserProfileImage?> = flow {
        val cachedImage = profileImageCacheManager.getProfileImage(userId).first()
        
        if (cachedImage != null) {
            emit(cachedImage)
        } else {
            try {
                val apiResult = getUserProfileImageUseCase(userId).first()
                if (apiResult.isSuccess) {
                    val profileImage = apiResult.getOrThrow()
                    profileImageCacheManager.saveProfileImage(userId, profileImage)
                    emit(profileImage)
                } else {
                    emit(null)
                }
            } catch (_: Exception) {
                emit(null)
            }
        }
    }
}
