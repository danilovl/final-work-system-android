package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finalworksystem.R
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
    Column {
        Text(
            text = stringResource(R.string.events_for_date, selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Text(
                    text = stringResource(R.string.error_prefix, errorMessage),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> {
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

                if (eventsForSelectedDate.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_events_for_date),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn {
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
}
