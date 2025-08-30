package com.finalworksystem.domain.conversation.model.response

import com.finalworksystem.domain.conversation.model.ConversationParticipant
import com.finalworksystem.domain.conversation.model.ConversationType
import com.finalworksystem.domain.conversation.model.ConversationWork
import com.finalworksystem.domain.conversation.model.LastMessage
import com.finalworksystem.domain.user.model.User

data class ConversationWorkResponse(
    val id: Int,
    val name: String?,
    val isRead: Boolean,
    val recipient: User?,
    val work: ConversationWork?,
    val participants: List<ConversationParticipant>,
    val lastMessage: LastMessage?,
    val type: ConversationType
)
