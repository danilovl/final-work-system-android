package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.view_model.task.TaskViewModel
import org.koin.compose.koinInject

@Composable
fun TaskItem(
    task: Task,
    onClick: ((Task) -> Unit)? = null,
    showSupervisorStatus: Boolean = false,
    taskViewModel: TaskViewModel? = null,
    userService: UserService = koinInject()
) {
    var isSupervisor by remember { mutableStateOf(false) }
    var isStudent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (showSupervisorStatus) {
            try {
                isSupervisor = userService.isSupervisor()
            } catch (_: Exception) {
                isSupervisor = false
            }
        }

        taskViewModel?.let {
            try {
                isStudent = it.isStudent()
            } catch (_: Exception) {
                isStudent = false
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
                    text = if (task.complete) "Completed" else "Pending",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (task.complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )

                if (isStudent && taskViewModel != null && task.work != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Notify Complete:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Checkbox(
                            checked = task.notifyComplete,
                            onCheckedChange = { isChecked ->
                                if (isChecked && !task.notifyComplete) {
                                    taskViewModel.notifyTaskComplete(task.id, task.work.id) { success -> }
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = if (task.notifyComplete) Color.Green else MaterialTheme.colorScheme.primary,
                                uncheckedColor = if (!task.notifyComplete) Color.Red else MaterialTheme.colorScheme.outline,
                                checkmarkColor = Color.White
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (showSupervisorStatus && isSupervisor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Active: ${if (task.active) "Yes" else "No"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (task.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Notify Complete: ${if (task.notifyComplete) "Yes" else "No"}",
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
                    text = "Deadline: ${if (task.deadline != null) DateUtils.formatToYmd(task.deadline) else "Not specified"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "ID: ${task.id}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (task.work != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Work: ${task.work.title}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Deadline: ${DateUtils.formatToYmd(task.work.deadline)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
