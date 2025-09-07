package com.finalworksystem.presentation.view_model.conversation.state

import com.finalworksystem.presentation.ui.component.MessagesState
import com.finalworksystem.presentation.view_model.conversation.state.MessagesState as ViewModelMessagesState

fun ViewModelMessagesState.toGeneric(): MessagesState {
    return when (this) {
        is ViewModelMessagesState.Idle -> MessagesState.Idle
        is ViewModelMessagesState.Loading -> MessagesState.Loading
        is ViewModelMessagesState.Success -> MessagesState.Success(
            messages = this.messages,
            hasMoreMessages = this.hasMoreMessages,
            isLoadingMore = this.isLoadingMore
        )
        is ViewModelMessagesState.Error -> MessagesState.Error(this.message)
    }
}
