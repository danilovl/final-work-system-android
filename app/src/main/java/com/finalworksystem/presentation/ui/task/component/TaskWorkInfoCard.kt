package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.task.model.WorkInfo
import com.finalworksystem.presentation.ui.component.BaseCard

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
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Work Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            TaskInfoItemWithIcon(
                icon = Icons.Default.Info,
                label = "Title",
                value = work.title,
                iconTint = MaterialTheme.colorScheme.primary,
                onClick = { onNavigateToWorkDetail(work.id) }
            )

            if (work.shortcut != null) {
                TaskInfoItemWithIcon(
                    icon = Icons.Default.Info,
                    label = "Shortcut",
                    value = work.shortcut,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { onNavigateToWorkDetail(work.id) }
                )
            }

            TaskInfoItemWithIcon(
                icon = Icons.Default.DateRange,
                label = "Deadline",
                value = DateUtils.formatToYmd(work.deadline),
                iconTint = MaterialTheme.colorScheme.error,
                onClick = { onNavigateToWorkDetail(work.id) }
            )

            if (work.deadlineProgram != null) {
                TaskInfoItemWithIcon(
                    icon = Icons.Default.DateRange,
                    label = "Program deadline",
                    value = DateUtils.formatToYmd(work.deadlineProgram),
                    iconTint = MaterialTheme.colorScheme.error,
                    onClick = { onNavigateToWorkDetail(work.id) }
                )
            }

            TaskInfoItemWithIcon(
                icon = Icons.Default.Info,
                label = "ID",
                value = work.id.toString(),
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { onNavigateToWorkDetail(work.id) }
            )
        }
    }
}