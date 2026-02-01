package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.domain.conversation.model.getDisplayName
import com.finalworksystem.presentation.ui.component.BaseCard

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit = {}
) {
    BaseCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .let { 
                if (!conversation.isRead) {
                    it.border(
                        width = 1.dp,
                        color = Color.Red,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    it
                }
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = conversation.getDisplayName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            conversation.recipient?.let { recipient ->
                Text(
                    text = stringResource(R.string.with_prefix, recipient.fullName ?: "${recipient.firstname} ${recipient.lastname}"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            conversation.work?.let { work ->
                Text(
                    text = stringResource(R.string.work_prefix, work.title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            conversation.lastMessage?.let { lastMessage ->
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = stringResource(R.string.last_message_by_prefix, lastMessage.owner.fullName ?: "${lastMessage.owner.firstname} ${lastMessage.owner.lastname}"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = DateUtils.formatToReadableDateTime(lastMessage.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
