package com.finalworksystem.presentation.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.finalworksystem.R
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.ui.component.PagedLazyColumn
import com.finalworksystem.presentation.ui.component.SearchModal
import com.finalworksystem.presentation.ui.component.SearchResetFloatingActionButton
import com.finalworksystem.presentation.ui.component.isRefreshing
import com.finalworksystem.presentation.ui.task.component.TaskItem
import com.finalworksystem.presentation.view_model.task.TaskListViewModel
import com.finalworksystem.presentation.view_model.task.TaskListViewModel.TaskListType
import com.finalworksystem.ui.designsystem.AppFab
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.theme.AppIcons

@Composable
fun TaskListOwnerScreen(
    taskListViewModel: TaskListViewModel,
    onNavigateBack: () -> Unit,
    onTaskClick: ((Int, Int) -> Unit)? = null,
    onNavigateToTaskCreate: ((Int) -> Unit)? = null
) {
    TaskListPage(
        type = TaskListType.OWNER,
        title = stringResource(R.string.task),
        taskListViewModel = taskListViewModel,
        onNavigateBack = onNavigateBack,
        onTaskClick = onTaskClick,
        onNavigateToTaskCreate = onNavigateToTaskCreate,
        showSupervisorStatus = true
    )
}

@Composable
fun TaskListSolverScreen(
    taskListViewModel: TaskListViewModel,
    onNavigateBack: () -> Unit,
    onTaskClick: ((Int, Int) -> Unit)? = null,
    onNavigateToTaskCreate: ((Int) -> Unit)? = null
) {
    TaskListPage(
        type = TaskListType.SOLVER,
        title = stringResource(R.string.assigned_task),
        taskListViewModel = taskListViewModel,
        onNavigateBack = onNavigateBack,
        onTaskClick = onTaskClick,
        onNavigateToTaskCreate = onNavigateToTaskCreate,
        showSupervisorStatus = false
    )
}

@Composable
private fun TaskListPage(
    type: TaskListType,
    title: String,
    taskListViewModel: TaskListViewModel,
    onNavigateBack: () -> Unit,
    onTaskClick: ((Int, Int) -> Unit)?,
    onNavigateToTaskCreate: ((Int) -> Unit)?,
    showSupervisorStatus: Boolean
) {
    val tasksFlow = remember(type) { taskListViewModel.tasks(type) }
    val tasks = tasksFlow.collectAsLazyPagingItems()
    val totalCount by taskListViewModel.totalCount(type).collectAsStateWithLifecycle()
    val searchQuery by taskListViewModel.searchQuery(type).collectAsStateWithLifecycle()
    var isSearchModalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(type) {
        taskListViewModel.loadTasks(type)
    }

    AppScaffold(
        topBar = {
            BaseTopAppBar(
                title = title,
                onNavigateBack = onNavigateBack,
                onReload = { taskListViewModel.refresh(type) },
                loadedCount = tasks.itemCount,
                // -1 means that the server did not report a total.
                totalCount = if (totalCount > 0) totalCount else -1,
                isLoading = tasks.isRefreshing
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onNavigateToTaskCreate != null) {
                    AppFab(
                        onClick = { onNavigateToTaskCreate(-1) },
                        icon = AppIcons.Add,
                        contentDescription = stringResource(R.string.create_task)
                    )
                }
                SearchResetFloatingActionButton(
                    searchQuery = searchQuery,
                    onSearchClick = { isSearchModalVisible = true },
                    onResetClick = { taskListViewModel.clearSearch(type) }
                )
            }
        }
    ) { paddingValues ->
        TaskList(
            tasks = tasks,
            onTaskClick = onTaskClick?.let { navigate ->
                { task -> navigate(task.work?.id ?: 0, task.id) }
            },
            showSupervisorStatus = showSupervisorStatus,
            modifier = Modifier.padding(paddingValues)
        )
    }

    SearchModal(
        isVisible = isSearchModalVisible,
        searchQuery = searchQuery,
        onSearchQueryChange = { taskListViewModel.updateSearchQuery(type, it) },
        onSearch = { taskListViewModel.performSearch(type, it) },
        onDismiss = { isSearchModalVisible = false }
    )
}

@Composable
fun TaskList(
    tasks: LazyPagingItems<Task>,
    onTaskClick: ((Task) -> Unit)?,
    showSupervisorStatus: Boolean,
    modifier: Modifier = Modifier
) {
    PagedLazyColumn(
        items = tasks,
        emptyText = stringResource(R.string.no_tasks_available),
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 160.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) { task ->
        TaskItem(
            task = task,
            onClick = onTaskClick,
            showSupervisorStatus = showSupervisorStatus
        )
    }
}
