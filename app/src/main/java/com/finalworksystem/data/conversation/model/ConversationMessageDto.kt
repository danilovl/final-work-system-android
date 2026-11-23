@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationMessageDto(
    val id: Int = 0,
    val owner: ConversationUserDto = ConversationUserDto(),
    val content: String = "",
    val isRead: Boolean = false,
    val createdAt: String = ""
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
