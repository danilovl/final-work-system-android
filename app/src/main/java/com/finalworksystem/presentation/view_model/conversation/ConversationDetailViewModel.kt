package com.finalworksystem.presentation.view_model.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.finalworksystem.application.use_case.conversation.CreateMessageUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationDetailUseCase
import com.finalworksystem.application.use_case.conversation.GetConversationMessagesUseCase
import com.finalworksystem.application.use_case.conversation.MarkMessageAsReadUseCase
import com.finalworksystem.data.util.isNotFound
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.presentation.view_model.conversation.state.ConversationDetailState
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.paging.Page
import com.finalworksystem.presentation.paging.PagedList
import com.finalworksystem.presentation.view_model.conversation.state.SendMessageState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationDetailViewModel(
    private val getConversationDetailUseCase: GetConversationDetailUseCase,
    private val getConversationMessagesUseCase: GetConversationMessagesUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val markMessageAsReadUseCase: MarkMessageAsReadUseCase,
    private val popupMessageService: PopupMessageService
) : ViewModel() {

    data class MessagesQuery(val conversationId: Int, val search: String? = null)

    private val _conversationDetailState = MutableStateFlow<ConversationDetailState>(ConversationDetailState.Idle)
    val conversationDetailState: StateFlow<ConversationDetailState> = _conversationDetailState.asStateFlow()

    private val pagedMessages = PagedList<MessagesQuery, ConversationMessage>(viewModelScope, MESSAGES_PAGE_SIZE) { query, page, pageSize ->
        getConversationMessagesUseCase(query.conversationId, page, pageSize, query.search).first()
            .map { Page(it.result, it.totalCount) }
    }

    /** Messages marked as read since the last load; updated without reloading the list. */
    private val readMessageIds = MutableStateFlow<Set<Int>>(emptySet())

    val messages: Flow<PagingData<ConversationMessage>> =
        combine(pagedMessages.items, readMessageIds) { data, readIds ->
            if (readIds.isEmpty()) data else data.map { if (it.id in readIds) it.copy(isRead = true) else it }
        }

    private val _sendMessageState = MutableStateFlow<SendMessageState>(SendMessageState.Idle)
    val sendMessageState: StateFlow<SendMessageState> = _sendMessageState.asStateFlow()

    private val _messageSearchQuery = MutableStateFlow("")
    val messageSearchQuery: StateFlow<String> = _messageSearchQuery.asStateFlow()


    fun loadConversationDetail(id: Int) {
        viewModelScope.launch {
            _conversationDetailState.value = ConversationDetailState.Loading

            getConversationDetailUseCase(id).collect { result ->
                result.fold(
                    onSuccess = { conversation ->
                        _conversationDetailState.value = ConversationDetailState.Success(conversation)
                    },
                    onFailure = { error ->
                        if (error.isNotFound()) {
                            _conversationDetailState.value = ConversationDetailState.NotFound
                            popupMessageService.showMessageResource(
                                com.finalworksystem.R.string.object_not_found,
                                PopupMessageService.MessageLevel.ERROR
                            )
                        } else {
                            val errorMessage = error.message ?: "Unknown error"
                            _conversationDetailState.value = ConversationDetailState.Error(errorMessage)
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    /** Shows the messages; they change often, so an already shown list is fetched again. */
    fun loadMessages(conversationId: Int, search: String? = null) {
        readMessageIds.value = emptySet()
        pagedMessages.reload(MessagesQuery(conversationId, search?.ifBlank { null }))
    }

    fun sendMessage(conversationId: Int, message: String) {
        viewModelScope.launch {
            _sendMessageState.value = SendMessageState.Loading

            createMessageUseCase(conversationId, message).collect { result ->
                result.fold(
                    onSuccess = {
                        _sendMessageState.value = SendMessageState.Success
                        loadMessages(conversationId, pagedMessages.query?.search)
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
                        readMessageIds.update { it + messageId }
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
        loadMessages(conversationId, query)
    }

    fun clearMessageSearch(conversationId: Int) {
        _messageSearchQuery.value = ""
        loadMessages(conversationId)
    }

    private companion object {
        const val MESSAGES_PAGE_SIZE = 10
    }
}
