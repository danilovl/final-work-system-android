package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.presentation.ui.component.BaseCard
import com.finalworksystem.ui.designsystem.AppIcon
import com.finalworksystem.ui.designsystem.AppIconButton
import com.finalworksystem.ui.designsystem.AppIconButtonVariant
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import com.finalworksystem.domain.event_calendar.model.CalendarEvent as DomainCalendarEvent

@Composable
fun EventItem(
    event: DomainCalendarEvent,
    onClick: () -> Unit,
    isSupervisor: Boolean = false,
    isStudent: Boolean = false,
    onDeleteEvent: ((DomainCalendarEvent) -> Unit)? = null,
    onReserveEvent: ((DomainCalendarEvent) -> Unit)? = null
) {
    val eventColor = parseColor(event.color)

    BaseCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .heightIn(min = 44.dp)
                    .background(color = eventColor, shape = RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppText(
                    text = event.title,
                    style = AppTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.onSurface
                )

                if (isSameDay(event.start, event.end)) {
                    EventMetaRow(
                        icon = AppIcons.CalendarToday,
                        text = stringResource(R.string.date_prefix, formatDate(event.start))
                    )
                    EventMetaRow(
                        icon = AppIcons.Today,
                        text = stringResource(R.string.time_range, formatTime(event.start), formatTime(event.end))
                    )
                } else {
                    EventMetaRow(
                        icon = AppIcons.CalendarToday,
                        text = stringResource(R.string.start_prefix, formatDateTime(event.start))
                    )
                    EventMetaRow(
                        icon = AppIcons.DateRange,
                        text = stringResource(R.string.end_prefix, formatDateTime(event.end))
                    )
                }
            }

            if (isStudent && !event.hasParticipant && onReserveEvent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                AppIconButton(
                    icon = AppIcons.BookmarkAdd,
                    contentDescription = stringResource(R.string.reserve_event),
                    onClick = { onReserveEvent(event) },
                    variant = AppIconButtonVariant.Tonal,
                    tint = AppTheme.colors.primary,
                    size = 38.dp,
                    iconSize = 20.dp
                )
            }

            if (isSupervisor && onDeleteEvent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                AppIconButton(
                    icon = AppIcons.Delete,
                    contentDescription = stringResource(R.string.delete_event),
                    onClick = { onDeleteEvent(event) },
                    variant = AppIconButtonVariant.Tonal,
                    tint = AppTheme.colors.error,
                    containerColor = AppTheme.colors.errorContainer,
                    size = 38.dp,
                    iconSize = 20.dp
                )
            }
        }
    }
}

@Composable
private fun EventMetaRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppIcon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = AppTheme.colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        AppText(
            text = text,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.onSurfaceVariant
        )
    }
}
