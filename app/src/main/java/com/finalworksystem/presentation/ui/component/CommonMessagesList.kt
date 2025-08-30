package com.finalworksystem.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.ui.conversation.component.MessageItem

@Composable
fun CommonMessagesList(
    messages: List<ConversationMessage>,
    hasMoreMessages: Boolean,
    isLoadingMore: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    currentUserId: Int,
    conversationId: Int,
    onMarkAsRead: (Int, Int) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false
) {
    val listState = rememberLazyListState()

    when {
        isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading messages: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        messages.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No messages")
            }
        }
        else -> {
            val shouldLoadMore = remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val firstVisibleItem = layoutInfo.visibleItemsInfo.firstOrNull()
                    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                    val totalItemsCount = layoutInfo.totalItemsCount

                    if (reverseLayout) {
                        lastVisibleItem != null &&
                        totalItemsCount > 0 &&
                        lastVisibleItem.index >= totalItemsCount - 3 &&
                        hasMoreMessages &&
                        !isLoadingMore
                    } else {
                        firstVisibleItem != null &&
                        totalItemsCount > 0 &&
                        firstVisibleItem.index <= 2 &&
                        listState.firstVisibleItemScrollOffset > 0 &&
                        hasMoreMessages &&
                        !isLoadingMore
                    }
                }
            }

            LaunchedEffect(shouldLoadMore.value) {
                if (shouldLoadMore.value) {
                    onLoadMore()
                }
            }

            LaunchedEffect(messages.isNotEmpty()) {
                if (messages.isNotEmpty() && !isLoadingMore) {
                    if (reverseLayout) {
                        listState.scrollToItem(0)
                    } else {
                        listState.scrollToItem(messages.size - 1)
                    }
                }
            }

            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty() && !isLoadingMore) {
                    if (!reverseLayout) {
                        val currentFirstVisible = listState.firstVisibleItemIndex
                        if (currentFirstVisible == 0 && messages.size > 10) {
                            listState.scrollToItem(10)
                        }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = reverseLayout
            ) {
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                items(
                    items = if (reverseLayout) messages.reversed() else messages,
                    key = { message -> message.id }
                ) { message ->
                    MessageItem(
                        message = message,
                        currentUserId = currentUserId,
                        conversationId = conversationId,
                        onMarkAsRead = onMarkAsRead
                    )
                }
            }
        }
    }
}
