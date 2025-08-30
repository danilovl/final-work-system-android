package com.finalworksystem.domain.conversation.model

import com.finalworksystem.domain.user.model.User

data class Conversation(
    val id: Int,
    val name: String?,
    val isRead: Boolean,
    val recipient: User?,
    val work: ConversationWork?,
    val lastMessage: LastMessage?,
    val participants: List<ConversationParticipant> = emptyList()
)

fun Conversation.getDisplayName(): String {
    return work?.title ?: (name ?: "Unnamed Conversation")
}
