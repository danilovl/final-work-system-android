@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.data.conversation.model.ConversationParticipantDto
import com.finalworksystem.data.conversation.model.ConversationTypeDto
import com.finalworksystem.data.conversation.model.ConversationUserDto
import com.finalworksystem.data.conversation.model.ConversationWorkDetailDto
import com.finalworksystem.data.conversation.model.LastMessageDto
import com.finalworksystem.data.conversation.model.toEntity
import com.finalworksystem.data.conversation.model.toEntityList
import com.finalworksystem.domain.conversation.model.response.ConversationWorkResponse
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import com.finalworksystem.infrastructure.api.serialization.UnwrappingSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = ConversationWorkResponseDtoSerializer::class)
data class ConversationWorkResponseDto(
    val id: Int = 0,
    val name: String? = null,
    val isRead: Boolean = false,
    val recipient: ConversationUserDto? = null,
    val work: ConversationWorkDetailDto? = null,
    val participants: List<ConversationParticipantDto> = emptyList(),
    val lastMessage: LastMessageDto? = null,
    val type: ConversationTypeDto = ConversationTypeDto(0, "", "")
)

/** Accepts the object either directly or wrapped in one of listOf("result", "work", "data"). */
object ConversationWorkResponseDtoSerializer : UnwrappingSerializer<ConversationWorkResponseDto>(
    ConversationWorkResponseDto.generatedSerializer(),
    listOf("result", "work", "data")
)

fun ConversationWorkResponseDto.toEntity(): ConversationWorkResponse {
    return ConversationWorkResponse(
        id = id,
        name = name,
        isRead = isRead,
        recipient = recipient?.toEntity(),
        work = work?.toEntity(),
        participants = participants.toEntityList(),
        lastMessage = lastMessage?.toEntity(),
        type = type.toEntity()
    )
}
