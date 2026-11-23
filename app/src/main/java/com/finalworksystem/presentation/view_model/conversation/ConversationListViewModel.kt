package com.finalworksystem.presentation.view_model.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.finalworksystem.application.use_case.conversation.GetConversationsWithPaginationUseCase
import com.finalworksystem.application.use_case.conversation.MarkAllMessagesAsReadUseCase
import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.presentation.paging.Page
import com.finalworksystem.presentation.paging.PagedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ConversationListViewModel(
    private val getConversationsWithPaginationUseCase: GetConversationsWithPaginationUseCase,
    private val markAllMessagesAsReadUseCase: MarkAllMessagesAsReadUseCase,
    private val popupMessageService: PopupMessageService
) : ViewModel() {

    data class Query(val search: String? = null)

    private val pagedConversations = PagedList<Query, Conversation>(viewModelScope, PAGE_SIZE) { query, page, pageSize ->
        getConversationsWithPaginationUseCase(page, pageSize, query.search).first()
            .map { Page(it.conversations, it.totalCount) }
    }

    /** Set after "mark all as read" succeeded, until the list is loaded again. */
    private val allMarkedAsRead = MutableStateFlow(false)

    val conversations: Flow<PagingData<Conversation>> =
        combine(pagedConversations.items, allMarkedAsRead) { data, allRead ->
            if (allRead) data.map { it.copy(isRead = true) } else data
        }

    val totalCount: StateFlow<Int> = pagedConversations.totalCount

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** Shows the conversations; they change often, so an already shown list is fetched again. */
    fun loadConversations() {
        allMarkedAsRead.value = false
        pagedConversations.reload(Query(_searchQuery.value.ifBlank { null }))
    }

    fun refresh() {
        allMarkedAsRead.value = false
        pagedConversations.refresh()
    }

    fun markAllMessagesAsRead() {
        viewModelScope.launch {
            markAllMessagesAsReadUseCase().collect { result ->
                result.fold(
                    onSuccess = { allMarkedAsRead.value = true },
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
        _searchQuery.value = query
        allMarkedAsRead.value = false
        pagedConversations.load(Query(query.ifBlank { null }))
    }

    fun clearSearch() {
        performSearch("")
    }

    private companion object {
        const val PAGE_SIZE = 10
    }
}
