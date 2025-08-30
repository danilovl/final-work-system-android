package com.finalworksystem.presentation.ui.work.component

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
import com.finalworksystem.presentation.ui.component.MessagesState

@Composable
fun WorkMessagesList(
    messagesState: MessagesState,
    currentUserId: Int,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (messagesState) {
        is MessagesState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is MessagesState.Success -> {
            WorkMessagesContent(
                messages = messagesState.messages,
                hasMoreMessages = messagesState.hasMoreMessages,
                isLoadingMore = messagesState.isLoadingMore,
                currentUserId = currentUserId,
                onLoadMore = onLoadMore,
                modifier = modifier
            )
        }
        is MessagesState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading messages: ${messagesState.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        else -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No messages")
            }
        }
    }
}

@Composable
private fun WorkMessagesContent(
    messages: List<ConversationMessage>,
    hasMoreMessages: Boolean,
    isLoadingMore: Boolean,
    currentUserId: Int,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    if (messages.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No messages")
        }
        return
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItemsCount = layoutInfo.totalItemsCount

            lastVisibleItem != null &&
            totalItemsCount > 0 &&
            lastVisibleItem.index >= totalItemsCount - 3 &&
            hasMoreMessages &&
            !isLoadingMore
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = messages,
            key = { message -> message.id }
        ) { message ->
            WorkMessageItem(
                message = message,
                currentUserId = currentUserId
            )
        }

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
    }
}
