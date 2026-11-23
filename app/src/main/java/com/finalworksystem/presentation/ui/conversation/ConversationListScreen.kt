package com.finalworksystem.presentation.ui.conversation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.infrastructure.popup.PopupMessageHandler
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.component.DoubleCheckIcon
import com.finalworksystem.presentation.ui.component.SearchModal
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.component.isRefreshing
import com.finalworksystem.presentation.ui.conversation.component.ConversationsList
import com.finalworksystem.presentation.view_model.conversation.ConversationListViewModel
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppSnackbarHostState

@Composable
fun ConversationListScreen(
    conversationListViewModel: ConversationListViewModel,
    popupMessageService: PopupMessageService,
    onNavigateBack: () -> Unit,
    onNavigateToConversationDetail: (Int) -> Unit
) {
    val conversations = conversationListViewModel.conversations.collectAsLazyPagingItems()
    val totalCount by conversationListViewModel.totalCount.collectAsStateWithLifecycle()
    val searchQuery by conversationListViewModel.searchQuery.collectAsStateWithLifecycle()
    val snackbarHostState = remember { AppSnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var isSearchModalVisible by remember { mutableStateOf(false) }

    PopupMessageHandler(
        popupMessageService = popupMessageService,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope
    )

    LaunchedEffect(Unit) {
        conversationListViewModel.loadConversations()
    }

    AppScaffold(
        topBar = {
            BaseTopAppBar(
                title = stringResource(R.string.conversation),
                onNavigateBack = onNavigateBack,
                onReload = { conversationListViewModel.refresh() },
                loadedCount = conversations.itemCount,
                totalCount = totalCount,
                isLoading = conversations.isRefreshing,
                additionalActions = {
                    AppIconButton(onClick = { conversationListViewModel.markAllMessagesAsRead() }) {
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
        ConversationsList(
            conversations = conversations,
            onConversationClick = onNavigateToConversationDetail,
            modifier = Modifier.padding(paddingValues)
        )
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
