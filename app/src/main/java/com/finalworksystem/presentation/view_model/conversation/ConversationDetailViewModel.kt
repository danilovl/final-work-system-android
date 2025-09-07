package com.finalworksystem.presentation.view_model.conversation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.conversation.CreateMessageUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationDetailUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationMessagesUseCase
import com.finalworksystem.application.use_case.conversation.MarkMessageAsReadUseCase
import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.infrastructure.popup.PopupMessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationDetailViewModel(
    application: Application,
    private val getConversationDetailUseCase: GetConversationDetailUseCase,
    private val getConversationMessagesUseCase: GetConversationMessagesUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val markMessageAsReadUseCase: MarkMessageAsReadUseCase,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    private val _conversationDetailState = MutableStateFlow<ConversationDetailState>(ConversationDetailState.Idle)
    val conversationDetailState: StateFlow<ConversationDetailState> = _conversationDetailState.asStateFlow()

    private val _messagesState = MutableStateFlow<MessagesState>(MessagesState.Idle)
    val messagesState: StateFlow<MessagesState> = _messagesState.asStateFlow()

    private val _sendMessageState = MutableStateFlow<SendMessageState>(SendMessageState.Idle)
    val sendMessageState: StateFlow<SendMessageState> = _sendMessageState.asStateFlow()

    private val _messageSearchQuery = MutableStateFlow("")
    val messageSearchQuery: StateFlow<String> = _messageSearchQuery.asStateFlow()

    private var currentMessagesPage = 1
    private val messagesPageSize = 10
    private var isLoadingMoreMessages = false
    private var currentSearch: String? = null

    fun loadConversationDetail(id: Int) {
        viewModelScope.launch {
            _conversationDetailState.value = ConversationDetailState.Loading

            getConversationDetailUseCase(id).collect { result ->
                result.fold(
                    onSuccess = { conversation ->
                        _conversationDetailState.value = ConversationDetailState.Success(conversation)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _conversationDetailState.value = ConversationDetailState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMessages(conversationId: Int, search: String? = null) {
        viewModelScope.launch {
            _messagesState.value = MessagesState.Loading
            currentMessagesPage = 1
            currentSearch = search

            getConversationMessagesUseCase(conversationId, currentMessagesPage, messagesPageSize, search).collect { result ->
                result.fold(
                    onSuccess = { messagesResponse ->
                        val hasMore = messagesResponse.result.size == messagesPageSize
                        _messagesState.value = MessagesState.Success(messagesResponse.result, hasMore, false)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _messagesState.value = MessagesState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreMessages(conversationId: Int) {
        val currentState = _messagesState.value
        if (currentState !is MessagesState.Success || !currentState.hasMoreMessages || isLoadingMoreMessages || currentState.isLoadingMore) {
            return
        }

        isLoadingMoreMessages = true
        _messagesState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            currentMessagesPage++

            getConversationMessagesUseCase(conversationId, currentMessagesPage, messagesPageSize, currentSearch).collect { result ->
                isLoadingMoreMessages = false
                result.fold(
                    onSuccess = { messagesResponse ->
                        val newMessages = messagesResponse.result
                        val currentMessages = currentState.messages
                        val updatedMessages = currentMessages + newMessages
                        val hasMore = newMessages.size == messagesPageSize

                        _messagesState.value = MessagesState.Success(updatedMessages, hasMore, false)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _messagesState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage("Failed to load more messages: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun sendMessage(conversationId: Int, message: String) {
        viewModelScope.launch {
            _sendMessageState.value = SendMessageState.Loading

            createMessageUseCase(conversationId, message).collect { result ->
                result.fold(
                    onSuccess = {
                        _sendMessageState.value = SendMessageState.Success
                        loadMessages(conversationId, currentSearch)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _sendMessageState.value = SendMessageState.Error(errorMessage)
                        popupMessageService.showMessage("Failed to send message: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun markMessageAsRead(conversationId: Int, messageId: Int) {
        viewModelScope.launch {
            markMessageAsReadUseCase(conversationId, messageId).collect { result ->
                result.fold(
                    onSuccess = {
                        val currentState = _messagesState.value
                        if (currentState is MessagesState.Success) {
                            val updatedMessages = currentState.messages.map { message ->
                                if (message.id == messageId) {
                                    message.copy(isRead = true)
                                } else {
                                    message
                                }
                            }
                            _messagesState.value = currentState.copy(messages = updatedMessages)
                        }
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        popupMessageService.showMessage("Failed to mark message as read: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun updateMessageSearchQuery(query: String) {
        _messageSearchQuery.value = query
    }

    fun performMessageSearch(conversationId: Int, query: String) {
        viewModelScope.launch { loadMessages(conversationId, query) }
    }

    fun clearMessageSearch(conversationId: Int) {
        _messageSearchQuery.value = ""
        loadMessages(conversationId)
    }

    sealed class ConversationDetailState {
        data object Idle : ConversationDetailState()
        data object Loading : ConversationDetailState()
        data class Success(val conversation: Conversation) : ConversationDetailState()
        data class Error(val message: String) : ConversationDetailState()
    }

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

    sealed class SendMessageState {
        data object Idle : SendMessageState()
        data object Loading : SendMessageState()
        data object Success : SendMessageState()
        data class Error(val message: String) : SendMessageState()
    }
}
