package com.finalworksystem.presentation.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.finalworksystem.R
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
import com.finalworksystem.presentation.view_model.task.TaskDetailViewModel
import com.finalworksystem.ui.designsystem.AppAlertDialog
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppEmptyState
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppLoadingState
import com.finalworksystem.ui.designsystem.AppScaffold
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TaskDetailScreen(
    workId: Int,
    taskId: Int,
    taskDetailViewModel: TaskDetailViewModel,
    userService: UserService,
    isOwner: Boolean = false,
    onNavigateBack: () -> Unit,
    onNavigateToWorkDetail: (Int) -> Unit
) {
    val taskDetailState by taskDetailViewModel.taskDetailState.collectAsStateWithLifecycle()
    var showTitleDialog by remember { mutableStateOf(false) }

    LaunchedEffect(workId, taskId) {
        taskDetailViewModel.loadTaskDetail(workId, taskId)
    }

    AppScaffold(
        topBar = {
            TaskDetailTopBar(
                taskDetailState = taskDetailState,
                onNavigateBack = onNavigateBack,
                onRefresh = { taskDetailViewModel.loadTaskDetail(workId, taskId) },
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
                is TaskDetailViewModel.TaskDetailState.Loading,
                is TaskDetailViewModel.TaskDetailState.Idle -> {
                    AppLoadingState()
                }
                is TaskDetailViewModel.TaskDetailState.Success -> {
                    TaskDetailContent(
                        task = state.task,
                        workId = workId,
                        taskDetailViewModel = taskDetailViewModel,
                        userService = userService,
                        isOwner = isOwner,
                        modifier = Modifier.fillMaxSize(),
                        onNavigateToWorkDetail = onNavigateToWorkDetail,
                        onNavigateBack = onNavigateBack
                    )
                }
                is TaskDetailViewModel.TaskDetailState.Error -> {
                    AppEmptyState(
                        title = stringResource(R.string.error_prefix, state.message),
                        icon = AppIcons.Info,
                        iconTint = AppTheme.colors.error,
                        action = {
                            AppButton(
                                text = stringResource(R.string.refresh),
                                onClick = { taskDetailViewModel.loadTaskDetail(workId, taskId) },
                                variant = AppButtonVariant.Secondary,
                                leadingIcon = AppIcons.Refresh
                            )
                        }
                    )
                }
            }
        }
    }

    val currentState = taskDetailState
    if (currentState is TaskDetailViewModel.TaskDetailState.Success) {
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
    taskDetailViewModel: TaskDetailViewModel,
    userService: UserService,
    isOwner: Boolean,
    modifier: Modifier = Modifier,
    onNavigateToWorkDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
            isStudent = taskDetailViewModel.isStudent()
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
                    taskDetailViewModel.changeTaskStatus(task.id, workId, status) { success ->
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
            AppAlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                icon = { AppIcon(imageVector = AppIcons.Delete, contentDescription = null) },
                title = { AppText(stringResource(R.string.delete_task)) },
                text = { AppText(stringResource(R.string.delete_task_confirm)) },
                confirmButton = {
                    AppButton(
                        text = stringResource(R.string.delete),
                        variant = AppButtonVariant.Danger,
                        onClick = {
                            showDeleteConfirmDialog = false
                            isDeletingTask = true
                            taskDetailViewModel.deleteTask(task.id, workId) { success ->
                                isDeletingTask = false
                                if (success) {
                                    onNavigateBack()
                                }
                            }
                        }
                    )
                },
                dismissButton = {
                    AppButton(
                        text = stringResource(R.string.cancel),
                        variant = AppButtonVariant.Ghost,
                        onClick = { showDeleteConfirmDialog = false }
                    )
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
                taskDetailViewModel.notifyTaskComplete(task.id, workId) { success ->
                    isNotifyingComplete = false
                }
            },
            onDeleteTaskClick = {
                showDeleteConfirmDialog = true
            }
        )
    }
}
