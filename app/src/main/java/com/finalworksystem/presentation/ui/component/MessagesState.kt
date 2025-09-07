package com.finalworksystem.presentation.ui.component

import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.view_model.work.WorkViewModel
import com.finalworksystem.presentation.view_model.conversation.ConversationDetailViewModel

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

fun ConversationDetailViewModel.MessagesState.toGeneric(): MessagesState {
    return when (this) {
        is ConversationDetailViewModel.MessagesState.Idle -> MessagesState.Idle
        is ConversationDetailViewModel.MessagesState.Loading -> MessagesState.Loading
        is ConversationDetailViewModel.MessagesState.Success -> MessagesState.Success(
            messages = this.messages,
            hasMoreMessages = this.hasMoreMessages,
            isLoadingMore = this.isLoadingMore
        )
        is ConversationDetailViewModel.MessagesState.Error -> MessagesState.Error(this.message)
    }
}
