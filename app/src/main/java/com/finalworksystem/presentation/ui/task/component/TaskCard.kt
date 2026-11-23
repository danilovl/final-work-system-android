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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.view_model.task.TaskDetailViewModel
import com.finalworksystem.ui.designsystem.AppCheckbox
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TaskCard(
    task: Task,
    taskViewModel: TaskDetailViewModel? = null,
    onClick: ((Task) -> Unit)? = null
) {
    var isStudent by remember { mutableStateOf(false) }

    LaunchedEffect(taskViewModel) {
        taskViewModel?.let {
            isStudent = it.isStudent()
        }
    }
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (onClick != null) { { onClick(task) } } else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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

                if (isStudent && taskViewModel != null && task.work != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AppCheckbox(
                        checked = task.notifyComplete,
                        onCheckedChange = { isChecked ->
                            if (isChecked && !task.notifyComplete) {
                                taskViewModel.notifyTaskComplete(task.id, task.work.id) { success -> }
                            }
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskStatusTags(task = task, showActive = false, showNotify = false)

                task.deadline?.let { deadline ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppIcon(
                            imageVector = AppIcons.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AppTheme.colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AppText(
                            text = DateUtils.formatToReadable(deadline),
                            style = AppTheme.typography.labelMedium,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
