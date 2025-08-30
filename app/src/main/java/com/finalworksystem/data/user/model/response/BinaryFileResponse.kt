package com.finalworksystem.data.user.model.response

import com.finalworksystem.domain.user.model.UserProfileImage
import okhttp3.ResponseBody

data class BinaryFileResponse(
    val fileName: String?,
    val contentType: String?,
    val contentLength: Long?,
    val responseBody: ResponseBody
)

fun BinaryFileResponse.toDomainModel(): UserProfileImage {
    return try {
        UserProfileImage(
            fileName = fileName,
            contentType = contentType,
            contentLength = contentLength,
            imageData = responseBody.bytes()
        )
    } catch (_: Exception) {
        UserProfileImage(
            fileName = fileName,
            contentType = contentType,
            contentLength = contentLength,
            imageData = ByteArray(0)
        )
    }
}
