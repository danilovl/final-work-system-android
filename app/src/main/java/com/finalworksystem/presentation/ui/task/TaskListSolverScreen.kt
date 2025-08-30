package com.finalworksystem.presentation.ui.task

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.finalworksystem.presentation.ui.component.BaseTopAppBar
import com.finalworksystem.presentation.view_model.task.TaskViewModel

@Composable
fun TaskListSolverScreen(
    taskViewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    onTaskClick: ((Int, Int) -> Unit)? = null
) {
    val tasksState by taskViewModel.tasksState.collectAsState()

    LaunchedEffect(Unit) {
        taskViewModel.loadTasksForSolver(forceRefresh = false)
    }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = "Assigned task",
                onNavigateBack = onNavigateBack,
                onReload = { taskViewModel.loadTasksForSolver(forceRefresh = true) },
                loadedCount = (tasksState as? TaskViewModel.TasksState.Success)?.tasks?.size ?: 0,
                totalCount = (tasksState as? TaskViewModel.TasksState.Success)?.totalCount?.let { if (it > 0) it else -1 } ?: -1,
                isLoading = tasksState is TaskViewModel.TasksState.Loading
            )
        }
    ) { paddingValues ->
        when (tasksState) {
            is TaskViewModel.TasksState.Loading -> {
                TaskListScreen(
                    tasks = emptyList(),
                    isLoading = true,
                    onTaskClick = onTaskClick?.let { navFunction ->
                        { task -> navFunction(task.work?.id ?: 0, task.id) }
                    },
                    taskViewModel = taskViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is TaskViewModel.TasksState.Success -> {
                val successState = tasksState as TaskViewModel.TasksState.Success
                TaskListScreen(
                    tasks = successState.tasks,
                    hasMoreTasks = successState.hasMoreTasks,
                    isLoadingMore = successState.isLoadingMore,
                    onTaskClick = onTaskClick?.let { navFunction ->
                        { task -> navFunction(task.work?.id ?: 0, task.id) }
                    },
                    onLoadMore = {
                        taskViewModel.loadMoreTasks()
                    },
                    taskViewModel = taskViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is TaskViewModel.TasksState.Error -> {
                TaskListScreen(
                    tasks = emptyList(),
                    onTaskClick = onTaskClick?.let { navFunction ->
                        { task -> navFunction(task.work?.id ?: 0, task.id) }
                    },
                    taskViewModel = taskViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                // nothing
            }
        }
    }
}
