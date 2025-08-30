package com.finalworksystem.data.conversation.model

data class ConversationParticipantDto(
    val id: Int,
    val user: ConversationUserDto
)

fun ConversationParticipantDto.toEntity(): com.finalworksystem.domain.conversation.model.ConversationParticipant {
    return com.finalworksystem.domain.conversation.model.ConversationParticipant(
        id = id,
        user = user.toEntity()
    )
}

fun List<ConversationParticipantDto>.toEntityList(): List<com.finalworksystem.domain.conversation.model.ConversationParticipant> {
    return map { it.toEntity() }
}
