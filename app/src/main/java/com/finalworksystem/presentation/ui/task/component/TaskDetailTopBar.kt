package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.BackNavigationButton
import com.finalworksystem.presentation.view_model.task.TaskDetailViewModel
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.designsystem.AppTopBar
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TaskDetailTopBar(
    taskDetailState: TaskDetailViewModel.TaskDetailState,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onTitleClick: () -> Unit
) {
    AppTopBar(
        title = {
            val taskTitle = when (taskDetailState) {
                is TaskDetailViewModel.TaskDetailState.Success -> taskDetailState.task.name
                else -> stringResource(R.string.task_detail)
            }
            AppText(
                text = taskTitle,
                style = AppTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (taskDetailState is TaskDetailViewModel.TaskDetailState.Success) {
                    Modifier.clickable { onTitleClick() }
                } else {
                    Modifier
                }
            )
        },
        navigationIcon = { BackNavigationButton(onClick = onNavigateBack) },
        actions = {
            AppIconButton(
                icon = AppIcons.Refresh,
                contentDescription = stringResource(R.string.refresh),
                onClick = onRefresh
            )
        }
    )
}
