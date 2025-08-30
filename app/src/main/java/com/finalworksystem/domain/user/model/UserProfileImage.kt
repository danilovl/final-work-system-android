package com.finalworksystem.domain.user.model

data class UserProfileImage(
    val fileName: String?,
    val contentType: String?,
    val contentLength: Long?,
    val imageData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserProfileImage

        if (fileName != other.fileName) return false
        if (contentType != other.contentType) return false
        if (contentLength != other.contentLength) return false
        if (!imageData.contentEquals(other.imageData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName?.hashCode() ?: 0
        result = 31 * result + (contentType?.hashCode() ?: 0)
        result = 31 * result + (contentLength?.hashCode() ?: 0)
        result = 31 * result + imageData.contentHashCode()
        return result
    }
}
