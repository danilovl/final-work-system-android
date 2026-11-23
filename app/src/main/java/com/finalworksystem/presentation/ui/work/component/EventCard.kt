package com.finalworksystem.presentation.ui.work.component

import com.finalworksystem.ui.theme.AppIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.finalworksystem.domain.common.util.DateUtils
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppTag
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppTheme

@Composable
fun EventCard(
    event: Event,
    onClick: ((Event) -> Unit)? = null
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (onClick != null) { { onClick(event) } } else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconBadge(icon = AppIcons.CalendarToday, size = 40)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    AppText(
                        text = event.name ?: event.type.name,
                        style = AppTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (event.name != null) {
                        AppText(
                            text = event.type.name,
                            style = AppTheme.typography.labelMedium,
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }

            event.participant?.let { participant ->
                EventMetaRow(
                    icon = AppIcons.Person,
                    text = "${participant.user.firstname} ${participant.user.lastname}"
                )
            }

            EventMetaRow(
                icon = AppIcons.DateRange,
                text = "${DateUtils.formatToReadableDateTime(event.start)} - ${DateUtils.formatToReadableDateTime(event.end)}"
            )

            event.address?.let { address ->
                EventMetaRow(
                    icon = AppIcons.LocationOn,
                    text = buildString {
                        address.name?.let { append(it) }
                        address.street?.let { street ->
                            if (isNotEmpty()) append(", ")
                            append(street)
                        }
                    }
                )
            }

            event.comment?.takeIf { it.isNotEmpty() }?.let { comments ->
                AppTag(
                    text = androidx.compose.ui.res.pluralStringResource(com.finalworksystem.R.plurals.comments_count, comments.size, comments.size),
                    color = AppTheme.colors.secondary,
                    icon = AppIcons.Comment
                )
            }
        }
    }
}

@Composable
private fun EventMetaRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = AppTheme.colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        AppText(
            text = text,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant
        )
    }
}
