package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.PagedLazyColumn
import com.finalworksystem.presentation.ui.task.component.TaskCard
import com.finalworksystem.presentation.view_model.work.WorkDetailViewModel
import com.finalworksystem.ui.designsystem.AppFab
import com.finalworksystem.ui.theme.AppIcons

@Composable
fun TaskTabContent(
    workDetailViewModel: WorkDetailViewModel,
    workId: Int,
    onNavigateToTaskDetail: ((Int, Int) -> Unit)? = null,
    onNavigateToTaskCreate: ((Int) -> Unit)? = null
) {
    val tasks = workDetailViewModel.tasks.collectAsLazyPagingItems()

    LaunchedEffect(workId) {
        workDetailViewModel.loadTasksForWork(workId)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        PagedLazyColumn(
            items = tasks,
            emptyText = stringResource(R.string.no_tasks_available),
            // Space at the bottom so the last card is not hidden behind the button.
            contentPadding = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) { task ->
            TaskCard(
                task = task,
                onClick = onNavigateToTaskDetail?.let { navigate -> { navigate(workId, task.id) } }
            )
        }

        if (onNavigateToTaskCreate != null) {
            AppFab(
                onClick = { onNavigateToTaskCreate(workId) },
                icon = AppIcons.Add,
                contentDescription = stringResource(R.string.create_task),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}
