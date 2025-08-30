package com.finalworksystem.presentation.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.task.model.TaskStatus
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.TitleDialog
import com.finalworksystem.presentation.ui.task.component.TaskActionsCard
import com.finalworksystem.presentation.ui.task.component.TaskBasicInfoCard
import com.finalworksystem.presentation.ui.task.component.TaskDescriptionCard
import com.finalworksystem.presentation.ui.task.component.TaskDetailTopBar
import com.finalworksystem.presentation.ui.task.component.TaskInformationCard
import com.finalworksystem.presentation.ui.task.component.TaskStatusDialog
import com.finalworksystem.presentation.ui.task.component.TaskWorkInfoCard
import com.finalworksystem.presentation.view_model.task.TaskViewModel

@Composable
fun TaskDetailScreen(
    workId: Int,
    taskId: Int,
    taskViewModel: TaskViewModel,
    userService: UserService,
    isOwner: Boolean = false,
    onNavigateBack: () -> Unit,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    val taskDetailState by taskViewModel.taskDetailState.collectAsState()
    var showTitleDialog by remember { mutableStateOf(false) }

    LaunchedEffect(workId, taskId) {
        taskViewModel.loadTaskDetail(workId, taskId)
    }

    Scaffold(
        topBar = {
            TaskDetailTopBar(
                taskDetailState = taskDetailState,
                onNavigateBack = onNavigateBack,
                onRefresh = { taskViewModel.loadTaskDetail(workId, taskId) },
                onTitleClick = { showTitleDialog = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = taskDetailState) {
                is TaskViewModel.TaskDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is TaskViewModel.TaskDetailState.Success -> {
                    TaskDetailContent(
                        task = state.task,
                        workId = workId,
                        taskViewModel = taskViewModel,
                        userService = userService,
                        isOwner = isOwner,
                        modifier = Modifier.fillMaxSize(),
                        onNavigateToWorkDetail = onNavigateToWorkDetail,
                        onNavigateBack = onNavigateBack
                    )
                }
                is TaskViewModel.TaskDetailState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is TaskViewModel.TaskDetailState.Idle -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    val currentState = taskDetailState
    if (currentState is TaskViewModel.TaskDetailState.Success) {
        TitleDialog(
            title = currentState.task.name,
            isVisible = showTitleDialog,
            onDismiss = { showTitleDialog = false }
        )
    }
}

@Composable
fun TaskDetailContent(
    task: Task,
    workId: Int,
    taskViewModel: TaskViewModel,
    userService: UserService,
    isOwner: Boolean,
    modifier: Modifier = Modifier,
    onNavigateToWorkDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskBasicInfoCard(task = task)

        TaskInformationCard(task = task)

        var isSupervisor by remember { mutableStateOf(false) }
        var isStudent by remember { mutableStateOf(false) }
        var showStatusDialog by remember { mutableStateOf(false) }
        var selectedStatus by remember { mutableStateOf<TaskStatus?>(null) }
        var isChangingStatus by remember { mutableStateOf(false) }
        var isNotifyingComplete by remember { mutableStateOf(false) }
        var isDeletingTask by remember { mutableStateOf(false) }
        var showDeleteConfirmDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            isSupervisor = userService.isSupervisor()
            isStudent = taskViewModel.isStudent()
        }

        TaskStatusDialog(
            showDialog = showStatusDialog,
            selectedStatus = selectedStatus,
            isChangingStatus = isChangingStatus,
            onDismiss = { 
                showStatusDialog = false
                selectedStatus = null
            },
            onStatusSelected = { status -> selectedStatus = status },
            onConfirm = {
                selectedStatus?.let { status ->
                    isChangingStatus = true
                    taskViewModel.changeTaskStatus(task.id, workId, status) { success ->
                        isChangingStatus = false
                        if (success) {
                            showStatusDialog = false
                            selectedStatus = null
                        }
                    }
                }
            }
        )

        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmDialog = false
                            isDeletingTask = true
                            taskViewModel.deleteTask(task.id, workId) { success ->
                                isDeletingTask = false
                                if (success) {
                                    onNavigateBack()
                                }
                            }
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        task.description?.let { description ->
            TaskDescriptionCard(description = description)
        }

        task.work?.let { work ->
            TaskWorkInfoCard(
                work = work,
                onNavigateToWorkDetail = onNavigateToWorkDetail
            )
        }

        TaskActionsCard(
            task = task,
            isSupervisor = isSupervisor,
            isOwner = isOwner,
            isStudent = isStudent,
            isChangingStatus = isChangingStatus,
            isNotifyingComplete = isNotifyingComplete,
            isDeletingTask = isDeletingTask,
            onChangeStatusClick = { showStatusDialog = true },
            onNotifyCompleteClick = {
                isNotifyingComplete = true
                taskViewModel.notifyTaskComplete(task.id, workId) { success ->
                    isNotifyingComplete = false
                }
            },
            onDeleteTaskClick = {
                showDeleteConfirmDialog = true
            }
        )
    }
}
