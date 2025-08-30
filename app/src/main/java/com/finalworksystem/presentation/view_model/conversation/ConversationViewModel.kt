package com.finalworksystem.presentation.view_model.conversation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.conversation.CreateMessageUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationDetailUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationMessagesUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationsWithPaginationUseCase
import com.finalworksystem.application.use_case.conversation.MarkAllMessagesAsReadUseCase
import com.finalworksystem.application.use_case.conversation.MarkMessageAsReadUseCase
import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.infrastructure.popup.PopupMessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    application: Application,
    private val getConversationsWithPaginationUseCase: GetConversationsWithPaginationUseCase,
    private val getConversationDetailUseCase: GetConversationDetailUseCase,
    private val getConversationMessagesUseCase: GetConversationMessagesUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val markMessageAsReadUseCase: MarkMessageAsReadUseCase,
    private val markAllMessagesAsReadUseCase: MarkAllMessagesAsReadUseCase,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    private val _conversationsState = MutableStateFlow<ConversationsState>(ConversationsState.Idle)
    val conversationsState: StateFlow<ConversationsState> = _conversationsState.asStateFlow()

    private val _conversationDetailState = MutableStateFlow<ConversationDetailState>(ConversationDetailState.Idle)
    val conversationDetailState: StateFlow<ConversationDetailState> = _conversationDetailState.asStateFlow()

    private val _messagesState = MutableStateFlow<MessagesState>(MessagesState.Idle)
    val messagesState: StateFlow<MessagesState> = _messagesState.asStateFlow()

    private val _sendMessageState = MutableStateFlow<SendMessageState>(SendMessageState.Idle)
    val sendMessageState: StateFlow<SendMessageState> = _sendMessageState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _messageSearchQuery = MutableStateFlow("")
    val messageSearchQuery: StateFlow<String> = _messageSearchQuery.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10
    private var isLoadingMore = false

    private var currentMessagesPage = 1
    private val messagesPageSize = 10
    private var isLoadingMoreMessages = false
    private var currentSearch: String? = null
    private var currentConversationSearch: String? = null

    fun loadConversations(search: String? = null) {
        viewModelScope.launch {
            _conversationsState.value = ConversationsState.Loading
            currentPage = 1
            currentConversationSearch = search

            getConversationsWithPaginationUseCase(currentPage, pageSize, search).collect { result ->
                result.fold(
                    onSuccess = { conversationsResponse ->
                        val conversations = conversationsResponse.conversations
                        val hasMore = conversationsResponse.currentItemCount + ((currentPage - 1) * pageSize) < conversationsResponse.totalCount

                        _conversationsState.value = ConversationsState.Success(conversations, hasMore, false, conversationsResponse.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _conversationsState.value = ConversationsState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreConversations() {
        val currentState = _conversationsState.value
        if (currentState !is ConversationsState.Success || !currentState.hasMoreConversations || isLoadingMore || currentState.isLoadingMore) {
            return
        }

        isLoadingMore = true
        _conversationsState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            currentPage++

            getConversationsWithPaginationUseCase(currentPage, pageSize, currentConversationSearch).collect { result ->
                isLoadingMore = false
                result.fold(
                    onSuccess = { paginatedConversations ->
                        val newConversations = paginatedConversations.conversations
                        val currentConversations = currentState.conversations
                        val updatedConversations = currentConversations + newConversations
                        val hasMore = updatedConversations.size < paginatedConversations.totalCount

                        _conversationsState.value = ConversationsState.Success(updatedConversations, hasMore, false, paginatedConversations.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _conversationsState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage("Failed to load more conversations: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun resetConversationsState() {
        _conversationsState.value = ConversationsState.Idle
    }

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
                        val hasMore = messagesResponse.currentItemCount + ((currentMessagesPage - 1) * messagesPageSize) < messagesResponse.totalCount
                        _messagesState.value = MessagesState.Success(messagesResponse.result.reversed(), hasMore)
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
                        val currentMessages = currentState.messages
                        val updatedMessages = messagesResponse.result.reversed() + currentMessages
                        val hasMore = messagesResponse.currentItemCount + ((currentMessagesPage - 1) * messagesPageSize) < messagesResponse.totalCount
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

    fun resetConversationDetailState() {
        _conversationDetailState.value = ConversationDetailState.Idle
    }

    fun resetMessagesState() {
        _messagesState.value = MessagesState.Idle
    }

    fun sendMessage(conversationId: Int, message: String) {
        viewModelScope.launch {
            _sendMessageState.value = SendMessageState.Loading

            createMessageUseCase(conversationId, message).collect { result ->
                result.fold(
                    onSuccess = {
                        _sendMessageState.value = SendMessageState.Success
                        popupMessageService.showMessage("Message sent successfully", PopupMessageService.MessageLevel.SUCCESS)
                        loadMessages(conversationId)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Failed to send message"
                        _sendMessageState.value = SendMessageState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun resetSendMessageState() {
        _sendMessageState.value = SendMessageState.Idle
    }

    fun markMessageAsRead(conversationId: Int, messageId: Int) {
        viewModelScope.launch {
            val currentState = _messagesState.value
            if (currentState is MessagesState.Success) {
                val targetMessage = currentState.messages.find { it.id == messageId }
                val newReadStatus = !(targetMessage?.isRead ?: false)
                val actionText = if (newReadStatus) "read" else "unread"

                markMessageAsReadUseCase(conversationId, messageId).collect { result ->
                    result.fold(
                        onSuccess = {
                            popupMessageService.showMessage("Message marked as $actionText", PopupMessageService.MessageLevel.SUCCESS)
                            val updatedMessages = currentState.messages.map { message ->
                                if (message.id == messageId) {
                                    message.copy(isRead = newReadStatus)
                                } else {
                                    message
                                }
                            }
                            _messagesState.value = currentState.copy(messages = updatedMessages)
                        },
                        onFailure = { error ->
                            val errorMessage = error.message ?: "Failed to change message status"
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        }
                    )
                }
            }
        }
    }

    fun markAllMessagesAsRead() {
        viewModelScope.launch {
            val currentState = _conversationsState.value
            if (currentState is ConversationsState.Success) {
                markAllMessagesAsReadUseCase().collect { result ->
                    result.fold(
                        onSuccess = {
                            popupMessageService.showMessage("All messages marked as read", PopupMessageService.MessageLevel.SUCCESS)
                            val updatedConversations = currentState.conversations.map { conversation ->
                                conversation.copy(isRead = true)
                            }
                            _conversationsState.value = currentState.copy(conversations = updatedConversations)
                        },
                        onFailure = { error ->
                            val errorMessage = error.message ?: "Failed to mark all messages as read"
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        }
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun performSearch(query: String) {
        loadConversations(search = query.takeIf { it.isNotBlank() })
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadConversations(search = null)
    }

    fun updateMessageSearchQuery(query: String) {
        _messageSearchQuery.value = query
    }

    fun performMessageSearch(conversationId: Int, query: String) {
        _messageSearchQuery.value = query
        loadMessages(conversationId, query.takeIf { it.isNotBlank() })
    }

    fun clearMessageSearch(conversationId: Int) {
        _messageSearchQuery.value = ""
        loadMessages(conversationId, null)
    }

    sealed class ConversationsState {
        object Idle : ConversationsState()
        object Loading : ConversationsState()
        data class Success(
            val conversations: List<Conversation>, 
            val hasMoreConversations: Boolean = false,
            val isLoadingMore: Boolean = false,
            val totalCount: Int = 0
        ) : ConversationsState()
        data class Error(val message: String) : ConversationsState()
    }

    sealed class ConversationDetailState {
        object Idle : ConversationDetailState()
        object Loading : ConversationDetailState()
        data class Success(val conversation: Conversation) : ConversationDetailState()
        data class Error(val message: String) : ConversationDetailState()
    }

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

    sealed class SendMessageState {
        object Idle : SendMessageState()
        object Loading : SendMessageState()
        object Success : SendMessageState()
        data class Error(val message: String) : SendMessageState()
    }
}
