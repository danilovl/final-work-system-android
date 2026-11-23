@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationDto(
    val id: Int = 0,
    val name: String? = null,
    val isRead: Boolean = false,
    val recipient: ConversationUserDto? = null,
    val work: ConversationWorkDto? = null,
    val lastMessage: LastMessageDto? = null,
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
