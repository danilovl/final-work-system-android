package com.finalworksystem.presentation.view_model.work.state

import com.finalworksystem.presentation.ui.component.MessagesState

fun WorkMessagesState.toGeneric(): MessagesState {
    return when (this) {
        is WorkMessagesState.Idle -> MessagesState.Idle
        is WorkMessagesState.Loading -> MessagesState.Loading
        is WorkMessagesState.Success -> MessagesState.Success(
            messages = this.messages,
            hasMoreMessages = this.hasMoreMessages,
            isLoadingMore = this.isLoadingMore
        )
        is WorkMessagesState.Error -> MessagesState.Error(this.message)
    }
}
