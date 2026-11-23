package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TaskBasicInfoCard(
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIconBadge(
                    icon = if (task.complete) AppIcons.CheckCircle else AppIcons.Task,
                    tint = if (task.complete) AppTheme.colors.success else AppTheme.colors.primary,
                    size = 44
                )
                Spacer(modifier = Modifier.width(12.dp))
                AppText(
                    text = task.name,
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }

            TaskStatusTags(task = task, showActive = true, showNotify = true)
        }
    }
}

/** Status tags of a task: completion (success / warning), active (primary), notified (warning). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TaskStatusTags(
    task: Task,
    showActive: Boolean,
    showNotify: Boolean,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (task.complete) {
            AppTag(
                text = stringResource(R.string.task_status_completed),
                color = AppTheme.colors.success,
                icon = AppIcons.CheckCircle
            )
        } else {
            AppTag(
                text = stringResource(R.string.task_status_pending),
                color = AppTheme.colors.error,
                icon = AppIcons.Info
            )
        }

        if (showActive) {
            AppTag(
                text = stringResource(R.string.active_prefix, if (task.active) stringResource(R.string.yes) else stringResource(R.string.no)),
                color = if (task.active) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant
            )
        }

        if (showNotify) {
            AppTag(
                text = stringResource(R.string.notify_complete_prefix, if (task.notifyComplete) stringResource(R.string.yes) else stringResource(R.string.no)),
                color = if (task.notifyComplete) AppTheme.colors.warning else AppTheme.colors.onSurfaceVariant,
                icon = AppIcons.Notifications
            )
        }
    }
}
