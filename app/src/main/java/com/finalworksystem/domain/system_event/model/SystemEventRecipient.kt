package com.finalworksystem.domain.system_event.model

import com.finalworksystem.domain.user.model.User

data class SystemEventRecipient(
    val id: Int,
    val systemEvent: SystemEvent,
    val user: User,
    val isRead: Boolean,
    val readAt: String?
)
