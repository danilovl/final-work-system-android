package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.data.conversation.model.ConversationParticipantDto
import com.finalworksystem.data.conversation.model.ConversationUserDto
import com.finalworksystem.data.conversation.model.ConversationWorkDto
import com.finalworksystem.data.conversation.model.LastMessageDto
import com.finalworksystem.data.conversation.model.toEntity
import com.google.gson.annotations.SerializedName

data class ConversationResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String?,

    @SerializedName("isRead")
    val isRead: Boolean,

    @SerializedName("recipient")
    val recipient: ConversationUserDto?,

    @SerializedName("work")
    val work: ConversationWorkDto?,

    @SerializedName("lastMessage")
    val lastMessage: LastMessageDto?,

    @SerializedName("participants")
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
