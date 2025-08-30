package com.finalworksystem.domain.conversation.model

import com.finalworksystem.domain.user.model.User

data class ConversationParticipant(
    val id: Int,
    val user: User
)
