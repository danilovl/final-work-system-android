package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.domain.conversation.model.getDisplayName
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppDivider
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit = {}
) {
    val colors = AppTheme.colors
    val isUnread = !conversation.isRead

    AppCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = if (isUnread) BorderStroke(1.5.dp, colors.primary.copy(alpha = 0.6f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconBadge(
                    icon = AppIcons.Comment,
                    tint = if (isUnread) colors.primary else colors.onSurfaceVariant,
                    size = 40
                )

                Spacer(modifier = Modifier.width(12.dp))

                AppText(
                    text = conversation.getDisplayName(),
                    style = AppTheme.typography.titleMedium,
                    fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (isUnread) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colors.primary, CircleShape)
                    )
                }
            }

            val hasDetails = conversation.recipient != null || conversation.work != null
            if (hasDetails) {
                Spacer(modifier = Modifier.height(12.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                conversation.recipient?.let { recipient ->
                    DetailLine(
                        icon = { AppIcon(AppIcons.Person, null, Modifier.size(16.dp), tint = colors.onSurfaceVariant) },
                        text = stringResource(R.string.with_prefix, recipient.fullName ?: "${recipient.firstname} ${recipient.lastname}")
                    )
                }

                conversation.work?.let { work ->
                    DetailLine(
                        icon = { AppIcon(AppIcons.Work, null, Modifier.size(16.dp), tint = colors.onSurfaceVariant) },
                        text = stringResource(R.string.work_prefix, work.title)
                    )
                }
            }

            conversation.lastMessage?.let { lastMessage ->
                AppDivider(modifier = Modifier.padding(vertical = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = stringResource(R.string.last_message_by_prefix, lastMessage.owner.fullName ?: "${lastMessage.owner.firstname} ${lastMessage.owner.lastname}"),
                        style = AppTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AppText(
                        text = DateUtils.formatToReadableDateTime(lastMessage.createdAt),
                        style = AppTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailLine(icon: @Composable () -> Unit, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        AppText(
            text = text,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
