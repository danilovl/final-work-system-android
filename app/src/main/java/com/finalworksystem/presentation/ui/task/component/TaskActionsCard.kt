package com.finalworksystem.presentation.ui.task.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.presentation.ui.component.BaseCard

@Composable
fun TaskActionsCard(
    task: Task,
    isSupervisor: Boolean,
    isOwner: Boolean,
    isStudent: Boolean,
    isChangingStatus: Boolean,
    isNotifyingComplete: Boolean,
    isDeletingTask: Boolean,
    onChangeStatusClick: () -> Unit,
    onNotifyCompleteClick: () -> Unit,
    onDeleteTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if ((isSupervisor && isOwner) || isStudent) {
        BaseCard(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.actions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (isSupervisor && isOwner) {
                    OutlinedButton(
                        onClick = onChangeStatusClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isChangingStatus
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.change_status))
                    }

                    OutlinedButton(
                        onClick = onDeleteTaskClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isDeletingTask && !isChangingStatus
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isDeletingTask) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.deleting))
                        } else {
                            Text(stringResource(R.string.delete_task))
                        }
                    }
                }

                if (isStudent) {
                    OutlinedButton(
                        onClick = onNotifyCompleteClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isNotifyingComplete && !task.notifyComplete
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (isNotifyingComplete) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.notifying))
                        } else {
                            Text(if (task.notifyComplete) stringResource(R.string.already_notified) else stringResource(R.string.notify_complete))
                        }
                    }
                }
            }
        }
    }
}
