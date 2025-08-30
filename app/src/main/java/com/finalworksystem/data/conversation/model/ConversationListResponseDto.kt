package com.finalworksystem.data.conversation.model

data class ConversationListResponseDto(
    val numItemsPerPage: Int,
    val totalCount: Int,
    val currentItemCount: Int,
    val result: List<ConversationDto>
)
