package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.presentation.ui.component.BaseCard

@Composable
fun TaskInformationCard(
    task: Task,
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
                text = "Task Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            TaskInfoItemWithIcon(
                icon = if (task.complete) Icons.Default.CheckCircle else Icons.Default.Info,
                label = "Status",
                value = if (task.complete) "Completed" else "Pending",
                iconTint = if (task.complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            TaskInfoItemWithIcon(
                icon = Icons.Default.Info,
                label = "Active",
                value = if (task.active) "Yes" else "No",
                iconTint = if (task.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            task.deadline?.let { deadline ->
                TaskInfoItemWithIcon(
                    icon = Icons.Default.DateRange,
                    label = "Deadline",
                    value = DateUtils.formatToYmd(deadline),
                    iconTint = MaterialTheme.colorScheme.error
                )
            }

            TaskInfoItemWithIcon(
                icon = Icons.Default.Notifications,
                label = "Notify on complete",
                value = if (task.notifyComplete) "Yes" else "No",
                iconTint = if (task.notifyComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            TaskInfoItemWithIcon(
                icon = Icons.Default.Info,
                label = "ID",
                value = task.id.toString(),
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
