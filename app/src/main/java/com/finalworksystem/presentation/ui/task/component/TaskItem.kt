package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BaseCard
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
        modifier = Modifier.padding(vertical = 8.dp),
        onClick = if (onClick != null) { { onClick(task) } } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (task.complete) stringResource(R.string.task_status_completed) else stringResource(R.string.task_status_pending),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (task.complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

            }

            if (showSupervisorStatus && isSupervisor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.active_prefix, if (task.active) stringResource(R.string.yes) else stringResource(R.string.no)),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (task.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.notify_complete_prefix, if (task.notifyComplete) stringResource(R.string.yes) else stringResource(R.string.no)),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (task.notifyComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.deadline_prefix, if (task.deadline != null) DateUtils.formatToYmd(task.deadline) else stringResource(R.string.not_specified)),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = stringResource(R.string.id_prefix, task.id.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (task.work != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.work_prefix, task.work.title),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.deadline_prefix, DateUtils.formatToYmd(task.work.deadline)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
