package com.finalworksystem.presentation.view_model.work.state

import com.finalworksystem.domain.conversation.model.ConversationMessage

sealed class WorkMessagesState {
    data object Idle : WorkMessagesState()
    data object Loading : WorkMessagesState()
    data class Success(
        val messages: List<ConversationMessage>,
        val hasMoreMessages: Boolean = false,
        val isLoadingMore: Boolean = false
    ) : WorkMessagesState()
    data class Error(val message: String) : WorkMessagesState()
}