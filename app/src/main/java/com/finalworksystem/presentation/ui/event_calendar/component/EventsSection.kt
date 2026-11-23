package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
import com.finalworksystem.ui.designsystem.AppBadge
import com.finalworksystem.ui.designsystem.AppCard
import com.finalworksystem.ui.designsystem.AppCardStyle
import com.finalworksystem.ui.designsystem.AppIconBadge
import com.finalworksystem.ui.designsystem.AppSectionHeader
import com.finalworksystem.ui.designsystem.AppSpinner
import com.finalworksystem.ui.designsystem.AppText
import com.finalworksystem.ui.theme.AppIcons
import com.finalworksystem.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.finalworksystem.domain.event_calendar.model.CalendarEvent as DomainCalendarEvent

@Composable
fun EventsSection(
    selectedDate: LocalDate,
    events: List<DomainCalendarEvent>,
    isLoading: Boolean,
    errorMessage: String?,
    onEventClick: ((DomainCalendarEvent) -> Unit)?,
    isSupervisor: Boolean = false,
    isStudent: Boolean = false,
    onDeleteEvent: ((DomainCalendarEvent) -> Unit)? = null,
    onReserveEvent: ((DomainCalendarEvent) -> Unit)? = null
) {
    val eventsForSelectedDate = events.filter { event ->
        try {
            val datePart = if (event.start.contains("T")) {
                event.start.split("T")[0]
            } else {
                event.start.split(" ")[0]
            }
            val eventDate = LocalDate.parse(datePart)
            eventDate == selectedDate
        } catch (_: Exception) {
            false
        }
    }

    Column {
        AppSectionHeader(
            title = stringResource(R.string.events_for_date, selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))),
            icon = AppIcons.CalendarToday,
            trailing = if (!isLoading && errorMessage == null && eventsForSelectedDate.isNotEmpty()) {
                { AppBadge(count = eventsForSelectedDate.size, color = AppTheme.colors.primary) }
            } else {
                null
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppSpinner()
                }
            }

            errorMessage != null -> {
                EventsMessage(
                    text = stringResource(R.string.error_prefix, errorMessage),
                    icon = AppIcons.Info,
                    tint = AppTheme.colors.error
                )
            }

            eventsForSelectedDate.isEmpty() -> {
                EventsMessage(
                    text = stringResource(R.string.no_events_for_date),
                    icon = AppIcons.DateRange,
                    tint = AppTheme.colors.onSurfaceVariant
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(eventsForSelectedDate) { event ->
                        EventItem(
                            event = event,
                            onClick = { onEventClick?.invoke(event) },
                            isSupervisor = isSupervisor,
                            isStudent = isStudent,
                            onDeleteEvent = onDeleteEvent,
                            onReserveEvent = onReserveEvent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsMessage(text: String, icon: ImageVector, tint: Color) {
    AppCard(
        style = AppCardStyle.Tonal,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppIconBadge(icon = icon, tint = tint, size = 36)
            Spacer(modifier = Modifier.width(12.dp))
            AppText(
                text = text,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}
