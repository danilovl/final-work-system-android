package com.finalworksystem.presentation.ui.component

import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.view_model.conversation.ConversationViewModel
import com.finalworksystem.presentation.view_model.work.WorkViewModel

sealed class MessagesState {
    object Idle : MessagesState()
    object Loading : MessagesState()
    data class Success(
        val messages: List<ConversationMessage>,
        val hasMoreMessages: Boolean = false,
        val isLoadingMore: Boolean = false
    ) : MessagesState()
    data class Error(val message: String) : MessagesState()
}

fun ConversationViewModel.MessagesState.toGeneric(): MessagesState {
    return when (this) {
        is ConversationViewModel.MessagesState.Idle -> MessagesState.Idle
        is ConversationViewModel.MessagesState.Loading -> MessagesState.Loading
        is ConversationViewModel.MessagesState.Success -> MessagesState.Success(
            messages = this.messages,
            hasMoreMessages = this.hasMoreMessages,
            isLoadingMore = this.isLoadingMore
        )
        is ConversationViewModel.MessagesState.Error -> MessagesState.Error(this.message)
    }
}

fun WorkViewModel.WorkMessagesState.toGeneric(): MessagesState {
    return when (this) {
        is WorkViewModel.WorkMessagesState.Idle -> MessagesState.Idle
        is WorkViewModel.WorkMessagesState.Loading -> MessagesState.Loading
        is WorkViewModel.WorkMessagesState.Success -> MessagesState.Success(
            messages = this.messages,
            hasMoreMessages = this.hasMoreMessages,
            isLoadingMore = this.isLoadingMore
        )
        is WorkViewModel.WorkMessagesState.Error -> MessagesState.Error(this.message)
    }
}
