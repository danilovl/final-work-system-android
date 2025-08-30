package com.finalworksystem.domain.conversation.model

import com.finalworksystem.domain.user.model.User

data class LastMessage(
    val id: Int,
    val owner: User,
    val createdAt: String
)
