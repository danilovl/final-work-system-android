package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppButton
import com.finalworksystem.ui.designsystem.AppButtonVariant
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.theme.AppIcons

@Composable
fun TaskActionsCard(
    task: Task,
    isSupervisor: Boolean,
    isOwner: Boolean,
    isStudent: Boolean,
    isChangingStatus: Boolean,
    isNotifyingComplete: Boolean,
    isDeletingTask: Boolean,
    onChangeStatusClick: () -> Unit,
    onNotifyCompleteClick: () -> Unit,
    onDeleteTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if ((isSupervisor && isOwner) || isStudent) {
        BaseCard(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppSectionHeader(
                    title = stringResource(R.string.actions),
                    icon = AppIcons.Edit
                )

                if (isSupervisor && isOwner) {
                    AppButton(
                        text = stringResource(R.string.change_status),
                        onClick = onChangeStatusClick,
                        modifier = Modifier.fillMaxWidth(),
                        variant = AppButtonVariant.Outline,
                        leadingIcon = AppIcons.Edit,
                        enabled = !isChangingStatus,
                        loading = isChangingStatus
                    )

                    AppButton(
                        text = if (isDeletingTask) stringResource(R.string.deleting) else stringResource(R.string.delete_task),
                        onClick = onDeleteTaskClick,
                        modifier = Modifier.fillMaxWidth(),
                        variant = AppButtonVariant.DangerSoft,
                        leadingIcon = AppIcons.Delete,
                        enabled = !isDeletingTask && !isChangingStatus,
                        loading = isDeletingTask
                    )
                }

                if (isStudent) {
                    AppButton(
                        text = when {
                            isNotifyingComplete -> stringResource(R.string.notifying)
                            task.notifyComplete -> stringResource(R.string.already_notified)
                            else -> stringResource(R.string.notify_complete)
                        },
                        onClick = onNotifyCompleteClick,
                        modifier = Modifier.fillMaxWidth(),
                        variant = if (task.notifyComplete) AppButtonVariant.Outline else AppButtonVariant.Primary,
                        leadingIcon = AppIcons.CheckCircle,
                        enabled = !isNotifyingComplete && !task.notifyComplete,
                        loading = isNotifyingComplete
                    )
                }
            }
        }
    }
}
