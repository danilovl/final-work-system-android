package com.finalworksystem.presentation.ui.conversation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.presentation.ui.component.rememberHtmlText
import com.finalworksystem.presentation.ui.user.component.UserAvatar
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun MessageItem(
    message: ConversationMessage,
    currentUserId: Int,
    conversationId: Int,
    onMarkAsRead: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val isOwner = message.owner.id == currentUserId
    val isUnread = !message.isRead

    val bubbleShape = if (isOwner) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 4.dp, bottomStart = 18.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp)
    }
    val bubbleBackground: Brush = if (isOwner) {
        Brush.linearGradient(listOf(colors.primary, colors.primaryGradientEnd))
    } else {
        SolidColor(colors.surface)
    }
    val bubbleBorder: BorderStroke? = when {
        isUnread && isOwner -> BorderStroke(1.5.dp, colors.onPrimary.copy(alpha = 0.6f))
        isUnread -> BorderStroke(1.5.dp, colors.primary)
        !isOwner -> BorderStroke(1.dp, colors.outlineVariant)
        else -> null
    }
    val textColor = if (isOwner) colors.onPrimary else colors.onSurface
    val secondaryTextColor = if (isOwner) colors.onPrimary.copy(alpha = 0.75f) else colors.onSurfaceVariant
    val content = if (isOwner) rememberOwnHtmlText(message.content, colors.onPrimary) else rememberHtmlText(message.content)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwner) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isOwner) {
            UserAvatar(
                firstname = message.owner.firstname,
                lastname = message.owner.lastname,
                userId = message.owner.id,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = 320.dp)
                .then(
                    if (!colors.isDark) {
                        Modifier.shadow(
                            elevation = 2.dp,
                            shape = bubbleShape,
                            clip = false,
                            ambientColor = colors.shadow.copy(alpha = 0.08f),
                            spotColor = colors.shadow.copy(alpha = 0.12f)
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(bubbleShape)
                .background(bubbleBackground, bubbleShape)
                .then(if (bubbleBorder != null) Modifier.border(bubbleBorder, bubbleShape) else Modifier)
        ) {
            Column(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 8.dp)
            ) {
                if (!isOwner) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppText(
                            text = message.owner.fullName ?: "${message.owner.firstname} ${message.owner.lastname}",
                            style = AppTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        AppIconButton(
                            icon = if (isUnread) AppIcons.Check else AppIcons.Close,
                            contentDescription = if (isUnread) stringResource(R.string.mark_as_viewed) else null,
                            onClick = { onMarkAsRead(conversationId, message.id) },
                            size = 26.dp,
                            iconSize = 16.dp,
                            tint = colors.primary
                        )
                    }
                }

                AppText(
                    text = content,
                    style = AppTheme.typography.bodyMedium,
                    color = textColor,
                    modifier = Modifier.padding(top = if (isOwner) 0.dp else 2.dp)
                )

                AppText(
                    text = DateUtils.formatToReadableDateTime(message.createdAt),
                    style = AppTheme.typography.labelSmall,
                    color = secondaryTextColor,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }

        if (!isOwner) {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

/** HTML content for own (primary colored) bubbles: links use the on-primary color to stay readable. */
@Composable
private fun rememberOwnHtmlText(html: String?, linkColor: Color): AnnotatedString =
    remember(html, linkColor) {
        if (html.isNullOrBlank()) {
            AnnotatedString("")
        } else {
            AnnotatedString.fromHtml(
                htmlString = html,
                linkStyles = TextLinkStyles(
                    style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
                )
            )
        }
    }
