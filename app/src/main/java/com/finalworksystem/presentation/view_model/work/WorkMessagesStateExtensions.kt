package com.finalworksystem.presentation.view_model.work

import com.finalworksystem.presentation.ui.component.MessagesState

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
