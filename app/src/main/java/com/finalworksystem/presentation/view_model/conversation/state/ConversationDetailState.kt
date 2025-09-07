package com.finalworksystem.presentation.view_model.conversation.state

import com.finalworksystem.domain.conversation.model.Conversation

sealed class ConversationDetailState {
    data object Idle : ConversationDetailState()
    data object Loading : ConversationDetailState()
    data class Success(val conversation: Conversation) : ConversationDetailState()
    data class Error(val message: String) : ConversationDetailState()
}