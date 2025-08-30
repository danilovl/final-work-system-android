package com.finalworksystem.domain.conversation.model

import com.finalworksystem.domain.user.model.User

data class ConversationMessage(
    val id: Int,
    val owner: User,
    val content: String,
    val isRead: Boolean,
    val createdAt: String
)
