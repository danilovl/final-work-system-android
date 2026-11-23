package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import org.koin.compose.koinInject

@Composable
fun TaskItem(
    task: Task,
    onClick: ((Task) -> Unit)? = null,
    showSupervisorStatus: Boolean = false,
    userService: UserService = koinInject()
) {
    var isSupervisor by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (showSupervisorStatus) {
            try {
                isSupervisor = userService.isSupervisor()
            } catch (_: Exception) {
                isSupervisor = false
            }
        }
    }
    BaseCard(
        onClick = if (onClick != null) { { onClick(task) } } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconBadge(
                    icon = if (task.complete) AppIcons.CheckCircle else AppIcons.Task,
                    tint = if (task.complete) AppTheme.colors.success else AppTheme.colors.primary,
                    size = 36
                )
                Spacer(modifier = Modifier.width(12.dp))
                AppText(
                    text = task.name,
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            TaskStatusTags(
                task = task,
                showActive = showSupervisorStatus && isSupervisor,
                showNotify = showSupervisorStatus && isSupervisor
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskMetaText(
                    icon = AppIcons.DateRange,
                    text = stringResource(
                        R.string.deadline_prefix,
                        if (task.deadline != null) DateUtils.formatToYmd(task.deadline) else stringResource(R.string.not_specified)
                    )
                )

                AppText(
                    text = stringResource(R.string.id_prefix, task.id.toString()),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.onSurfaceVariant
                )
            }

            if (task.work != null) {
                AppDivider()

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TaskMetaText(
                        icon = AppIcons.Work,
                        text = stringResource(R.string.work_prefix, task.work.title),
                        emphasized = true
                    )
                    TaskMetaText(
                        icon = AppIcons.CalendarToday,
                        text = stringResource(R.string.deadline_prefix, DateUtils.formatToYmd(task.work.deadline))
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskMetaText(
    icon: ImageVector,
    text: String,
    emphasized: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppIcon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = AppTheme.colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        AppText(
            text = text,
            style = AppTheme.typography.bodySmall,
            color = if (emphasized) AppTheme.colors.onSurface else AppTheme.colors.onSurfaceVariant,
            fontWeight = if (emphasized) FontWeight.SemiBold else null,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
