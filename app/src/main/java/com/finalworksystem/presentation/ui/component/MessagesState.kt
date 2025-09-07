package com.finalworksystem.presentation.ui.component

import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel
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

fun WorkDetailViewModel.WorkMessagesState.toGeneric(): MessagesState {
    return when (this) {
        is WorkDetailViewModel.WorkMessagesState.Idle -> MessagesState.Idle
        is WorkDetailViewModel.WorkMessagesState.Loading -> MessagesState.Loading
        is WorkDetailViewModel.WorkMessagesState.Success -> MessagesState.Success(
            messages = this.messages,
            hasMoreMessages = this.hasMoreMessages,
            isLoadingMore = this.isLoadingMore
        )
        is WorkDetailViewModel.WorkMessagesState.Error -> MessagesState.Error(this.message)
    }
}
