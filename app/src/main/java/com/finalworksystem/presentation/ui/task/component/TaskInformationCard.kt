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
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TaskInformationCard(
    task: Task,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppSectionHeader(
                title = stringResource(R.string.task_information),
                icon = AppIcons.Info
            )

            TaskInfoItemWithIcon(
                icon = if (task.complete) AppIcons.CheckCircle else AppIcons.Info,
                label = stringResource(R.string.status),
                value = if (task.complete) stringResource(R.string.task_status_completed) else stringResource(R.string.task_status_pending),
                iconTint = if (task.complete) AppTheme.colors.success else AppTheme.colors.error
            )

            TaskInfoItemWithIcon(
                icon = AppIcons.Check,
                label = stringResource(R.string.active),
                value = if (task.active) stringResource(R.string.yes) else stringResource(R.string.no),
                iconTint = if (task.active) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant
            )

            task.deadline?.let { deadline ->
                TaskInfoItemWithIcon(
                    icon = AppIcons.DateRange,
                    label = stringResource(R.string.deadline),
                    value = DateUtils.formatToYmd(deadline),
                    iconTint = AppTheme.colors.error
                )
            }

            TaskInfoItemWithIcon(
                icon = AppIcons.Notifications,
                label = stringResource(R.string.notify_on_complete),
                value = if (task.notifyComplete) stringResource(R.string.yes) else stringResource(R.string.no),
                iconTint = if (task.notifyComplete) AppTheme.colors.warning else AppTheme.colors.onSurfaceVariant
            )

            TaskInfoItemWithIcon(
                icon = AppIcons.Info,
                label = stringResource(R.string.id),
                value = task.id.toString(),
                iconTint = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}
