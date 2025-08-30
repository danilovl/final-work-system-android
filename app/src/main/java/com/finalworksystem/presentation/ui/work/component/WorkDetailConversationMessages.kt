package com.finalworksystem.presentation.ui.work.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.finalworksystem.presentation.ui.component.toGeneric
import com.finalworksystem.presentation.view_model.work.WorkViewModel

@Composable
fun WorkDetailConversationMessages(
    workId: Int,
    currentUserId: Int,
    workViewModel: WorkViewModel,
    modifier: Modifier = Modifier
) {
    val workMessagesState by workViewModel.workMessagesState.collectAsState()

    LaunchedEffect(workId) {
        workViewModel.loadWorkMessages(workId)
    }

    WorkMessagesList(
        messagesState = workMessagesState.toGeneric(),
        currentUserId = currentUserId,
        onLoadMore = { workViewModel.loadMoreWorkMessages(workId) },
        modifier = modifier
    )
}
