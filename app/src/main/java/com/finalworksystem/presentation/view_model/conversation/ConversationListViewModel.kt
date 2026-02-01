package com.finalworksystem.presentation.view_model.conversation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.conversation.GetConversationsWithPaginationUseCase
import com.finalworksystem.application.use_case.conversation.MarkAllMessagesAsReadUseCase
import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.infrastructure.popup.PopupMessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationListViewModel(
    application: Application,
    private val getConversationsWithPaginationUseCase: GetConversationsWithPaginationUseCase,
    private val markAllMessagesAsReadUseCase: MarkAllMessagesAsReadUseCase,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    private val _conversationsState = MutableStateFlow<ConversationsState>(ConversationsState.Idle)
    val conversationsState: StateFlow<ConversationsState> = _conversationsState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10
    private var isLoadingMore = false
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
                        val errorMessage = error.message
                        if (errorMessage != null) {
                            _conversationsState.value = ConversationsState.Error(errorMessage)
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        } else {
                            val fallbackMessage = getApplication<Application>().getString(com.finalworksystem.R.string.unknown_error)
                            _conversationsState.value = ConversationsState.Error(fallbackMessage)
                            popupMessageService.showMessageResource(com.finalworksystem.R.string.unknown_error, PopupMessageService.MessageLevel.ERROR)
                        }
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

    fun markAllMessagesAsRead() {
        viewModelScope.launch {
            markAllMessagesAsReadUseCase().collect { result ->
                result.fold(
                    onSuccess = {
                        val currentState = _conversationsState.value
                        if (currentState is ConversationsState.Success) {
                            val updatedConversations = currentState.conversations.map { conversation ->
                                conversation.copy(isRead = true)
                            }
                            _conversationsState.value = currentState.copy(conversations = updatedConversations)
                        }
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        popupMessageService.showMessage("Failed to mark messages as read: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun performSearch(query: String) {
        viewModelScope.launch { loadConversations(query) }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadConversations()
    }

    sealed class ConversationsState {
        data object Idle : ConversationsState()
        data object Loading : ConversationsState()
        data class Success(
            val conversations: List<Conversation>,
            val hasMoreConversations: Boolean = false,
            val isLoadingMore: Boolean = false,
            val totalCount: Int = 0
        ) : ConversationsState()
        data class Error(val message: String) : ConversationsState()
    }
}
