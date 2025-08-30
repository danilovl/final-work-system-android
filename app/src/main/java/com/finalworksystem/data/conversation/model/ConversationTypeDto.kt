package com.finalworksystem.data.conversation.model

data class ConversationTypeDto(
    val id: Int,
    val name: String,
    val constant: String
)

fun ConversationTypeDto.toEntity(): com.finalworksystem.domain.conversation.model.ConversationType {
    return com.finalworksystem.domain.conversation.model.ConversationType(
        id = id,
        name = name,
        constant = constant
    )
}
