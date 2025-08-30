package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.finalworksystem.presentation.view_model.task.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailTopBar(
    taskDetailState: TaskViewModel.TaskDetailState,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onTitleClick: () -> Unit
) {
    TopAppBar(
        title = {
            val taskTitle = when (taskDetailState) {
                is TaskViewModel.TaskDetailState.Success -> taskDetailState.task.name
                else -> "Task detail"
            }
            Text(
                text = taskTitle,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (taskDetailState is TaskViewModel.TaskDetailState.Success) {
                    Modifier.clickable { onTitleClick() }
                } else {
                    Modifier
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    )
}