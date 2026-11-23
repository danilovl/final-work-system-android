package com.finalworksystem.presentation.ui.work.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.presentation.ui.component.rememberHtmlText
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.ui.user.component.UserAvatar
import com.finalworksystem.ui.designsystem.AppSurface
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun WorkMessageItem(
    message: ConversationMessage,
    currentUserId: Int,
    modifier: Modifier = Modifier
) {
    val isOwner = message.owner.id == currentUserId
    val colors = AppTheme.colors
    val containerColor = if (isOwner) colors.primaryContainer else colors.surfaceVariant
    val contentColor = if (isOwner) colors.onPrimaryContainer else colors.onSurfaceVariant
    val bubbleShape = if (isOwner) {
        RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomEnd = 18.dp, bottomStart = 18.dp)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwner) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        if (isOwner) {
            UserAvatar(
                firstname = message.owner.firstname,
                lastname = message.owner.lastname,
                userId = message.owner.id
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        AppSurface(
            modifier = Modifier.weight(1f, fill = false),
            shape = bubbleShape,
            color = containerColor,
            contentColor = contentColor,
            // Unread messages are highlighted with an error-coloured outline.
            border = if (!message.isRead) BorderStroke(2.dp, colors.error) else null
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                AppText(
                    text = "${message.owner.firstname} ${message.owner.lastname}",
                    style = AppTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )

                AppText(
                    text = rememberHtmlText(message.content),
                    style = AppTheme.typography.bodyMedium,
                    color = contentColor,
                    modifier = Modifier.padding(top = 4.dp)
                )

                AppText(
                    text = DateUtils.formatToReadableDateTime(message.createdAt),
                    style = AppTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 6.dp)
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
