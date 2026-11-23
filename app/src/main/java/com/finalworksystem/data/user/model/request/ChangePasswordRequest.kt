package com.finalworksystem.data.user.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    @SerialName("currentPassword")
    val currentPassword: String,

    @SerialName("newPassword")
    val newPassword: String
)
