package com.finalworksystem.presentation.view_model.conversation.state

import com.finalworksystem.domain.conversation.model.ConversationMessage

sealed class MessagesState {
    data object Idle : MessagesState()
    data object Loading : MessagesState()
    data class Success(
        val messages: List<ConversationMessage>,
        val hasMoreMessages: Boolean = false,
        val isLoadingMore: Boolean = false
    ) : MessagesState()
    data class Error(val message: String) : MessagesState()
}