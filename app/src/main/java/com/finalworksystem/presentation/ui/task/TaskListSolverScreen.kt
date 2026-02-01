package com.finalworksystem.presentation.ui.task

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.view_model.task.TaskListViewModel

@Composable
fun TaskListSolverScreen(
    taskListViewModel: TaskListViewModel,
    onNavigateBack: () -> Unit,
    onTaskClick: ((Int, Int) -> Unit)? = null
) {
    val tasksState by taskListViewModel.tasksState.collectAsState()

    LaunchedEffect(Unit) {
        taskListViewModel.loadTasksForSolver(forceRefresh = false)
    }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = stringResource(R.string.assigned_task),
                onNavigateBack = onNavigateBack,
                onReload = { taskListViewModel.loadTasksForSolver(forceRefresh = true) },
                loadedCount = (tasksState as? TaskListViewModel.TasksState.Success)?.tasks?.size ?: 0,
                totalCount = (tasksState as? TaskListViewModel.TasksState.Success)?.totalCount?.let { if (it > 0) it else -1 } ?: -1,
                isLoading = tasksState is TaskListViewModel.TasksState.Loading
            )
        }
    ) { paddingValues ->
        when (tasksState) {
            is TaskListViewModel.TasksState.Loading -> {
                TaskListScreen(
                    tasks = emptyList(),
                    isLoading = true,
                    onTaskClick = onTaskClick?.let { navFunction ->
                        { task -> navFunction(task.work?.id ?: 0, task.id) }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is TaskListViewModel.TasksState.Success -> {
                val successState = tasksState as TaskListViewModel.TasksState.Success
                TaskListScreen(
                    tasks = successState.tasks,
                    hasMoreTasks = successState.hasMoreTasks,
                    isLoadingMore = successState.isLoadingMore,
                    onTaskClick = onTaskClick?.let { navFunction ->
                        { task -> navFunction(task.work?.id ?: 0, task.id) }
                    },
                    onLoadMore = {
                        taskListViewModel.loadMoreTasks()
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is TaskListViewModel.TasksState.Error -> {
                TaskListScreen(
                    tasks = emptyList(),
                    onTaskClick = onTaskClick?.let { navFunction ->
                        { task -> navFunction(task.work?.id ?: 0, task.id) }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                // nothing
            }
        }
    }
}