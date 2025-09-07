package com.finalworksystem.presentation.ui.work.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.finalworksystem.presentation.view_model.work.toGeneric
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel

@Composable
fun WorkDetailConversationMessages(
    workId: Int,
    currentUserId: Int,
    workDetailViewModel: WorkDetailViewModel,
    modifier: Modifier = Modifier
) {
    val workMessagesState by workDetailViewModel.workMessagesState.collectAsState()

    LaunchedEffect(workId) {
        workDetailViewModel.loadWorkMessages(workId)
    }

    WorkMessagesList(
        messagesState = workMessagesState.toGeneric(),
        currentUserId = currentUserId,
        onLoadMore = { workDetailViewModel.loadMoreWorkMessages(workId) },
        modifier = modifier
    )
}
