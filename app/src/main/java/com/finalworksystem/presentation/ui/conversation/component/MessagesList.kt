package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.ui.component.PagedLazyColumn

/**
 * Messages of a conversation in the order returned by the API;
 * further pages are loaded while scrolling down.
 */
@Composable
fun MessagesList(
    messages: LazyPagingItems<ConversationMessage>,
    currentUserId: Int,
    conversationId: Int,
    onMarkAsRead: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    PagedLazyColumn(
        items = messages,
        emptyText = stringResource(R.string.no_messages),
        modifier = modifier,
        contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) { message ->
        MessageItem(
            message = message,
            currentUserId = currentUserId,
            conversationId = conversationId,
            onMarkAsRead = onMarkAsRead
        )
    }
}
