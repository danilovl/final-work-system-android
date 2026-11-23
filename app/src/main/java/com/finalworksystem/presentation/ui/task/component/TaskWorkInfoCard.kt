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
import com.finalworksystem.domain.task.model.WorkInfo
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun TaskWorkInfoCard(
    work: WorkInfo,
    onNavigateToWorkDetail: (Int) -> Unit,
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
                title = stringResource(R.string.work_information),
                icon = AppIcons.Work
            )

            TaskInfoItemWithIcon(
                icon = AppIcons.Work,
                label = stringResource(R.string.title),
                value = work.title,
                iconTint = AppTheme.colors.primary,
                onClick = { onNavigateToWorkDetail(work.id) }
            )

            if (work.shortcut != null) {
                TaskInfoItemWithIcon(
                    icon = AppIcons.Info,
                    label = stringResource(R.string.shortcut),
                    value = work.shortcut,
                    iconTint = AppTheme.colors.onSurfaceVariant,
                    onClick = { onNavigateToWorkDetail(work.id) }
                )
            }

            TaskInfoItemWithIcon(
                icon = AppIcons.DateRange,
                label = stringResource(R.string.deadline),
                value = DateUtils.formatToYmd(work.deadline),
                iconTint = AppTheme.colors.error,
                onClick = { onNavigateToWorkDetail(work.id) }
            )

            if (work.deadlineProgram != null) {
                TaskInfoItemWithIcon(
                    icon = AppIcons.CalendarToday,
                    label = stringResource(R.string.program_deadline),
                    value = DateUtils.formatToYmd(work.deadlineProgram),
                    iconTint = AppTheme.colors.warning,
                    onClick = { onNavigateToWorkDetail(work.id) }
                )
            }

            TaskInfoItemWithIcon(
                icon = AppIcons.Info,
                label = stringResource(R.string.id),
                value = work.id.toString(),
                iconTint = AppTheme.colors.onSurfaceVariant,
                onClick = { onNavigateToWorkDetail(work.id) }
            )
        }
    }
}
