package com.finalworksystem.data.conversation.model

data class ConversationDto(
    val id: Int,
    val name: String?,
    val isRead: Boolean,
    val recipient: ConversationUserDto?,
    val work: ConversationWorkDto?,
    val lastMessage: LastMessageDto?,
    val participants: List<ConversationParticipantDto>? = null
)

fun ConversationDto.toEntity(): com.finalworksystem.domain.conversation.model.Conversation {
    return com.finalworksystem.domain.conversation.model.Conversation(
        id = id,
        name = name,
        isRead = isRead,
        recipient = recipient?.toEntity(),
        work = work?.toEntity(),
        lastMessage = lastMessage?.toEntity(),
        participants = participants?.map { it.toEntity() } ?: emptyList()
    )
}

fun List<ConversationDto>.toEntityList(): List<com.finalworksystem.domain.conversation.model.Conversation> {
    return map { it.toEntity() }
}

fun ConversationDto.getDisplayName(): String {
    return work?.title ?: (name ?: "Unnamed Conversation")
}
