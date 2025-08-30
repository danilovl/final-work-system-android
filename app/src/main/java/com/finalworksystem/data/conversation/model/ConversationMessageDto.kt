package com.finalworksystem.data.conversation.model

data class ConversationMessageDto(
    val id: Int,
    val owner: ConversationUserDto,
    val content: String,
    val isRead: Boolean,
    val createdAt: String
)

fun ConversationMessageDto.toEntity(): com.finalworksystem.domain.conversation.model.ConversationMessage {
    return com.finalworksystem.domain.conversation.model.ConversationMessage(
        id = id,
        owner = owner.toEntity(),
        content = content,
        isRead = isRead,
        createdAt = createdAt
    )
}

fun List<ConversationMessageDto>.toEntityList(): List<com.finalworksystem.domain.conversation.model.ConversationMessage> {
    return map { it.toEntity() }
}
