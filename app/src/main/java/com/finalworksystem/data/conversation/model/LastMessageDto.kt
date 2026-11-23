@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class LastMessageDto(
    val id: Int = 0,
    val owner: ConversationUserDto = ConversationUserDto(),
    val createdAt: String = ""
)

fun LastMessageDto.toEntity(): com.finalworksystem.domain.conversation.model.LastMessage {
    return com.finalworksystem.domain.conversation.model.LastMessage(
        id = id,
        owner = owner.toEntity(),
        createdAt = createdAt
    )
}
