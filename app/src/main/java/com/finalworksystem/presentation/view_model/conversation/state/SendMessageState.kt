package com.finalworksystem.presentation.view_model.conversation.state

sealed class SendMessageState {
    data object Idle : SendMessageState()
    data object Loading : SendMessageState()
    data object Success : SendMessageState()
    data class Error(val message: String) : SendMessageState()
}