package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.PagedLazyColumn
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel

@Composable
fun WorkDetailConversationMessages(
    workId: Int,
    currentUserId: Int,
    workDetailViewModel: WorkDetailViewModel,
    modifier: Modifier = Modifier
) {
    val messages = workDetailViewModel.workMessages.collectAsLazyPagingItems()

    LaunchedEffect(workId) {
        workDetailViewModel.loadWorkMessages(workId)
    }

    PagedLazyColumn(
        items = messages,
        emptyText = stringResource(R.string.no_messages),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) { message ->
        WorkMessageItem(
            message = message,
            currentUserId = currentUserId
        )
    }
}
