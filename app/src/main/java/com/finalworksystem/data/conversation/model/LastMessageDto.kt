package com.finalworksystem.data.conversation.model

data class LastMessageDto(
    val id: Int,
    val owner: ConversationUserDto,
    val createdAt: String
)

fun LastMessageDto.toEntity(): com.finalworksystem.domain.conversation.model.LastMessage {
    return com.finalworksystem.domain.conversation.model.LastMessage(
        id = id,
        owner = owner.toEntity(),
        createdAt = createdAt
    )
}
