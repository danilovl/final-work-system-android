package com.finalworksystem.domain.common.util

object FileUtils {

    fun formatFileSize(bytes: Int): String {
        return when {
            bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            bytes >= 1024 -> "${bytes / 1024} KB"
            else -> "$bytes B"
        }
    }
}