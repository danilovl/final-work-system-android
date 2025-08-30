package com.finalworksystem.infrastructure.cache

import android.util.Base64
import com.finalworksystem.domain.user.model.UserProfileImage
import com.finalworksystem.infrastructure.session.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileImageCacheManager(private val sessionManager: SessionManager) {
    companion object {
        private fun getUserProfileImageKey(userId: Int) = "profile_image_$userId"
    }

    private val gson = Gson()

    suspend fun saveProfileImage(userId: Int, userProfileImage: UserProfileImage) {
        val imageJson = gson.toJson(mapOf(
            "fileName" to userProfileImage.fileName,
            "contentType" to userProfileImage.contentType,
            "contentLength" to userProfileImage.contentLength,
            "imageData" to Base64.encodeToString(userProfileImage.imageData, Base64.DEFAULT)
        ))
        sessionManager.writeData(getUserProfileImageKey(userId), imageJson)
    }

    fun getProfileImage(userId: Int): Flow<UserProfileImage?> = sessionManager.readData(getUserProfileImageKey(userId)).map { imageJson ->
        if (imageJson != null) {
            try {
                @Suppress("UNCHECKED_CAST")
                val imageMap = gson.fromJson(imageJson, Map::class.java) as Map<String, Any>
                UserProfileImage(
                    fileName = imageMap["fileName"] as String?,
                    contentType = imageMap["contentType"] as String?,
                    contentLength = (imageMap["contentLength"] as Double?)?.toLong(),
                    imageData = Base64.decode(imageMap["imageData"] as String, Base64.DEFAULT)
                )
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }
    }

    suspend fun clearProfileImage(userId: Int) {
        sessionManager.removeData(getUserProfileImageKey(userId))
    }
}
