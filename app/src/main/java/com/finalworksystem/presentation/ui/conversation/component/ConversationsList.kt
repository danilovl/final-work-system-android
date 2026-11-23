package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.presentation.ui.component.PagedLazyColumn

@Composable
fun ConversationsList(
    conversations: LazyPagingItems<Conversation>,
    onConversationClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    PagedLazyColumn(
        items = conversations,
        emptyText = stringResource(R.string.no_conversations_found),
        modifier = modifier
    ) { conversation ->
        ConversationItem(
            conversation = conversation,
            onClick = { onConversationClick(conversation.id) }
        )
    }
}
