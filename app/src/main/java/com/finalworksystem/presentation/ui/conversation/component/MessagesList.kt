package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.finalworksystem.presentation.ui.component.CommonMessagesList
import com.finalworksystem.presentation.ui.component.MessagesState

@Composable
fun MessagesList(
    messagesState: MessagesState,
    currentUserId: Int,
    conversationId: Int,
    onMarkAsRead: (Int, Int) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false
) {
    when (messagesState) {
        is MessagesState.Loading -> {
            CommonMessagesList(
                messages = emptyList(),
                hasMoreMessages = false,
                isLoadingMore = false,
                isLoading = true,
                errorMessage = null,
                currentUserId = currentUserId,
                conversationId = conversationId,
                onMarkAsRead = onMarkAsRead,
                onLoadMore = onLoadMore,
                modifier = modifier,
                reverseLayout = reverseLayout
            )
        }
        is MessagesState.Success -> {
            CommonMessagesList(
                messages = messagesState.messages,
                hasMoreMessages = messagesState.hasMoreMessages,
                isLoadingMore = messagesState.isLoadingMore,
                isLoading = false,
                errorMessage = null,
                currentUserId = currentUserId,
                conversationId = conversationId,
                onMarkAsRead = onMarkAsRead,
                onLoadMore = onLoadMore,
                modifier = modifier,
                reverseLayout = reverseLayout
            )
        }
        is MessagesState.Error -> {
            CommonMessagesList(
                messages = emptyList(),
                hasMoreMessages = false,
                isLoadingMore = false,
                isLoading = false,
                errorMessage = messagesState.message,
                currentUserId = currentUserId,
                conversationId = conversationId,
                onMarkAsRead = onMarkAsRead,
                onLoadMore = onLoadMore,
                modifier = modifier,
                reverseLayout = reverseLayout
            )
        }
        else -> {
            CommonMessagesList(
                messages = emptyList(),
                hasMoreMessages = false,
                isLoadingMore = false,
                isLoading = false,
                errorMessage = null,
                currentUserId = currentUserId,
                conversationId = conversationId,
                onMarkAsRead = onMarkAsRead,
                onLoadMore = onLoadMore,
                modifier = modifier,
                reverseLayout = reverseLayout
            )
        }
    }
}
