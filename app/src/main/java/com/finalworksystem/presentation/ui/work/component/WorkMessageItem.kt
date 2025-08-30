package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.common.util.HtmlUtils
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.ui.user.component.UserAvatar

@Composable
fun WorkMessageItem(
    message: ConversationMessage,
    currentUserId: Int,
    modifier: Modifier = Modifier
) {
    val isOwner = message.owner.id == currentUserId

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwner) Arrangement.Start else Arrangement.End
    ) {
        if (isOwner) {
            UserAvatar(
                firstname = message.owner.firstname,
                lastname = message.owner.lastname,
                userId = message.owner.id
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier
                .weight(1f, fill = false)
                .let {
                    if (!message.isRead) {
                        it.border(
                            width = 2.dp,
                            color = Color.Red,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        it
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = if (isOwner) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${message.owner.firstname} ${message.owner.lastname}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOwner) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = HtmlUtils.fromHtml(message.content),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOwner) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = DateUtils.formatToReadableDateTime(message.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!isOwner) {
            Spacer(modifier = Modifier.width(8.dp))
            UserAvatar(
                firstname = message.owner.firstname,
                lastname = message.owner.lastname,
                userId = message.owner.id
            )
        }
    }
}
