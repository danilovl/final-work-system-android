package com.finalworksystem.domain.conversation.model.response

import com.finalworksystem.domain.conversation.model.ConversationMessage

data class ConversationMessagesResponse(
    val numItemsPerPage: Int,
    val totalCount: Int,
    val currentItemCount: Int,
    val result: List<ConversationMessage>
)
