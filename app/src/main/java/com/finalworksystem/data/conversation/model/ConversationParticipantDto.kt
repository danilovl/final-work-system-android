@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationParticipantDto(
    val id: Int = 0,
    val user: ConversationUserDto = ConversationUserDto()
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
