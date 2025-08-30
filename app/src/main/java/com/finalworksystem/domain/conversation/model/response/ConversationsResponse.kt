package com.finalworksystem.domain.conversation.model.response

import com.finalworksystem.domain.conversation.model.Conversation

data class ConversationsResponse(
    val conversations: List<Conversation>,
    val totalCount: Int,
    val currentItemCount: Int,
    val numItemsPerPage: Int
)
