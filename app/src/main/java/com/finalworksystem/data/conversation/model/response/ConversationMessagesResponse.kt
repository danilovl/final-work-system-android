package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.data.conversation.model.ConversationMessageDto
import com.finalworksystem.data.conversation.model.toEntityList
import com.finalworksystem.domain.conversation.model.response.ConversationMessagesResponse

data class ConversationMessagesResponseDto(
    val numItemsPerPage: Int,
    val totalCount: Int,
    val currentItemCount: Int,
    val result: List<ConversationMessageDto>
)

fun ConversationMessagesResponseDto.toEntity(): ConversationMessagesResponse {
    return ConversationMessagesResponse(
        numItemsPerPage = numItemsPerPage,
        totalCount = totalCount,
        currentItemCount = currentItemCount,
        result = result.toEntityList()
    )
}
