package com.finalworksystem.presentation.ui.conversation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.finalworksystem.infrastructure.popup.PopupMessageHandler
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.component.DoubleCheckIcon
import com.finalworksystem.presentation.ui.component.SearchModal
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.conversation.component.ConversationsList
import com.finalworksystem.presentation.view_model.conversation.ConversationListViewModel

@Composable
fun ConversationListScreen(
    conversationListViewModel: ConversationListViewModel,
    popupMessageService: PopupMessageService,
    onNavigateBack: () -> Unit,
    onNavigateToConversationDetail: (Int) -> Unit
) {
    val conversationsState by conversationListViewModel.conversationsState.collectAsState()
    val searchQuery by conversationListViewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var isSearchModalVisible by remember { mutableStateOf(false) }

    PopupMessageHandler(
        popupMessageService = popupMessageService,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope
    )

    LaunchedEffect(Unit) {
        val currentSearchQuery = searchQuery.takeIf { it.isNotBlank() }
        conversationListViewModel.loadConversations(search = currentSearchQuery)
    }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = "Conversation",
                onNavigateBack = onNavigateBack,
                onReload = { conversationListViewModel.loadConversations() },
                loadedCount = (conversationsState as? ConversationListViewModel.ConversationsState.Success)?.conversations?.size ?: 0,
                totalCount = (conversationsState as? ConversationListViewModel.ConversationsState.Success)?.totalCount ?: 0,
                isLoading = conversationsState is ConversationListViewModel.ConversationsState.Loading,
                additionalActions = {
                    IconButton(onClick = { conversationListViewModel.markAllMessagesAsRead() }) {
                        DoubleCheckIcon()
                    }
                }
            )
        },
        floatingActionButton = {
            SearchResetFloatingActionButton(
                searchQuery = searchQuery,
                onSearchClick = { isSearchModalVisible = true },
                onResetClick = { conversationListViewModel.clearSearch() }
            )
        }
    ) { paddingValues ->
        when (conversationsState) {
            is ConversationListViewModel.ConversationsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ConversationListViewModel.ConversationsState.Success -> {
                val successState = conversationsState as ConversationListViewModel.ConversationsState.Success
                ConversationsList(
                    conversations = successState.conversations,
                    hasMoreConversations = successState.hasMoreConversations,
                    isLoadingMore = successState.isLoadingMore,
                    onLoadMore = { conversationListViewModel.loadMoreConversations() },
                    onConversationClick = onNavigateToConversationDetail,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is ConversationListViewModel.ConversationsState.Error -> {
                val errorState = conversationsState as ConversationListViewModel.ConversationsState.Error
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${errorState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ConversationListViewModel.ConversationsState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading conversations...")
                }
            }
        }
    }

    SearchModal(
        isVisible = isSearchModalVisible,
        searchQuery = searchQuery,
        onSearchQueryChange = { conversationListViewModel.updateSearchQuery(it) },
        onSearch = { query ->
            conversationListViewModel.performSearch(query)
        },
        onDismiss = { isSearchModalVisible = false }
    )
}