package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.ui.task.component.TaskCard
import com.finalworksystem.presentation.view_model.work.WorkViewModel

@Composable
fun TaskTabContent(
    workViewModel: WorkViewModel,
    workId: Int,
    onNavigateToTaskDetail: ((Int, Int) -> Unit)? = null
) {
    val tasksState by workViewModel.tasksState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(workId) {
        workViewModel.loadTasksForWork(workId, forceRefresh = false)
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            totalItemsNumber > 0 && lastVisibleItemIndex > (totalItemsNumber - 3)
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && tasksState is WorkViewModel.TasksState.Success) {
            val currentState = tasksState as WorkViewModel.TasksState.Success
            if (currentState.hasMoreTasks && !currentState.isLoadingMore) {
                workViewModel.loadMoreTasks(workId)
            }
        }
    }

    when (tasksState) {
        is WorkViewModel.TasksState.Loading -> {
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        is WorkViewModel.TasksState.Success -> {
            val successState = tasksState as WorkViewModel.TasksState.Success
            val tasks = successState.tasks
            if (tasks.isEmpty()) {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No tasks available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                LazyColumn(state = listState) {
                    items(tasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = if (onNavigateToTaskDetail != null) { { onNavigateToTaskDetail(workId, task.id) } } else null
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    if (successState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        is WorkViewModel.TasksState.Error -> {
            val errorState = tasksState as WorkViewModel.TasksState.Error
            val errorMessage = errorState.message
            BaseCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Error loading tasks: $errorMessage",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
        else -> {
        }
    }
}
