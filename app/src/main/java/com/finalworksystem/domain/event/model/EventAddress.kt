package com.finalworksystem.domain.event.model

import com.finalworksystem.domain.user.model.User

data class EventAddress(
    val id: Int,
    val name: String?,
    val description: String?,
    val street: String?,
    val skype: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val owner: User
)
