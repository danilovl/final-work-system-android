package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.data.conversation.model.ConversationParticipantDto
import com.finalworksystem.data.conversation.model.ConversationTypeDto
import com.finalworksystem.data.conversation.model.ConversationUserDto
import com.finalworksystem.data.conversation.model.ConversationWorkDetailDto
import com.finalworksystem.data.conversation.model.LastMessageDto
import com.finalworksystem.data.conversation.model.toEntity
import com.finalworksystem.data.conversation.model.toEntityList
import com.finalworksystem.domain.conversation.model.response.ConversationWorkResponse

data class ConversationWorkResponseDto(
    val id: Int,
    val name: String?,
    val isRead: Boolean,
    val recipient: ConversationUserDto?,
    val work: ConversationWorkDetailDto?,
    val participants: List<ConversationParticipantDto>,
    val lastMessage: LastMessageDto?,
    val type: ConversationTypeDto
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
