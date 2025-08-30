package com.finalworksystem.data.conversation.model

import com.finalworksystem.domain.conversation.model.response.ConversationsResponse

data class PaginatedConversationsDto(
    val conversations: List<ConversationDto>,
    val totalCount: Int,
    val currentItemCount: Int,
    val numItemsPerPage: Int
)

fun PaginatedConversationsDto.toEntity(): ConversationsResponse {
    return ConversationsResponse(
        conversations = conversations.toEntityList(),
        totalCount = totalCount,
        currentItemCount = currentItemCount,
        numItemsPerPage = numItemsPerPage
    )
}
