package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.task.model.TaskStatus
import com.finalworksystem.ui.designsystem.AppAlertDialog
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppRadioRow
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TaskStatusDialog(
    showDialog: Boolean,
    selectedStatus: TaskStatus?,
    isChangingStatus: Boolean,
    onDismiss: () -> Unit,
    onStatusSelected: (TaskStatus) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialog) {
        AppAlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier,
            icon = { AppIcon(imageVector = AppIcons.Edit, contentDescription = null) },
            title = {
                AppText(stringResource(R.string.change_task_status))
            },
            text = {
                Column {
                    AppText(text = stringResource(R.string.select_new_status))

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        TaskStatus.values().forEach { status ->
                            AppRadioRow(
                                selected = selectedStatus == status,
                                onClick = { onStatusSelected(status) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AppTag(
                                    text = status.value.replaceFirstChar { it.uppercase() },
                                    color = taskStatusColor(status),
                                    icon = taskStatusIcon(status)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                AppButton(
                    text = stringResource(R.string.change_status),
                    onClick = onConfirm,
                    enabled = selectedStatus != null && !isChangingStatus,
                    loading = isChangingStatus
                )
            },
            dismissButton = {
                AppButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismiss,
                    variant = AppButtonVariant.Ghost
                )
            }
        )
    }
}

@Composable
private fun taskStatusColor(status: TaskStatus): Color = when (status) {
    TaskStatus.COMPLETE -> AppTheme.colors.success
    TaskStatus.ACTIVE -> AppTheme.colors.primary
    TaskStatus.NOTIFY -> AppTheme.colors.warning
}

@Composable
private fun taskStatusIcon(status: TaskStatus) = when (status) {
    TaskStatus.COMPLETE -> AppIcons.CheckCircle
    TaskStatus.ACTIVE -> AppIcons.Task
    TaskStatus.NOTIFY -> AppIcons.Notifications
}
