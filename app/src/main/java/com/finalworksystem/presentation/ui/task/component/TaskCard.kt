package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.presentation.view_model.task.TaskDetailViewModel

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (task.complete) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (task.complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                task.deadline?.let { deadline ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = DateUtils.formatToReadable(deadline),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (task.complete) stringResource(R.string.task_status_completed) else stringResource(R.string.task_status_pending),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (task.complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )

                    if (isStudent && taskViewModel != null && task.work != null) {
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
        }
    }
}
