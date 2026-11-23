@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.data.conversation.model.ConversationParticipantDto
import com.finalworksystem.data.conversation.model.ConversationUserDto
import com.finalworksystem.data.conversation.model.ConversationWorkDto
import com.finalworksystem.data.conversation.model.LastMessageDto
import com.finalworksystem.data.conversation.model.toEntity
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String? = null,

    @SerialName("isRead")
    val isRead: Boolean = false,

    @SerialName("recipient")
    val recipient: ConversationUserDto? = null,

    @SerialName("work")
    val work: ConversationWorkDto? = null,

    @SerialName("lastMessage")
    val lastMessage: LastMessageDto? = null,

    @SerialName("participants")
    val participants: List<ConversationParticipantDto>? = null
)

fun ConversationResponse.toDomain(): com.finalworksystem.domain.conversation.model.Conversation {
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

fun List<ConversationResponse>.toDomainList(): List<com.finalworksystem.domain.conversation.model.Conversation> {
    return map { it.toDomain() }
}

fun ConversationResponse.getDisplayName(): String {
    return work?.title ?: (name ?: "Unnamed Conversation")
}
