package com.finalworksystem.presentation.ui.event_calendar.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import com.finalworksystem.domain.event_calendar.model.CalendarEvent as DomainCalendarEvent

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    eventsForMonth: List<DomainCalendarEvent>
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Column {
        var dayCounter = 1
        val weeksInMonth = kotlin.math.ceil((daysInMonth + firstDayOfWeek) / 7.0).toInt()

        repeat(weeksInMonth) { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { dayOfWeek ->
                    val dayIndex = week * 7 + dayOfWeek

                    if (dayIndex >= firstDayOfWeek && dayCounter <= daysInMonth) {
                        val currentDate = currentMonth.atDay(dayCounter)
                        val eventsForDate = eventsForMonth.filter { event ->
                            try {
                                val datePart = if (event.start.contains("T")) {
                                    event.start.split("T")[0]
                                } else {
                                    event.start.split(" ")[0]
                                }
                                val eventDate = LocalDate.parse(datePart)
                                eventDate == currentDate
                            } catch (_: Exception) {
                                false
                            }
                        }

                        CalendarDay(
                            date = currentDate,
                            isSelected = currentDate == selectedDate,
                            eventsForDate = eventsForDate,
                            onClick = { onDateSelected(currentDate) },
                            modifier = Modifier.weight(1f)
                        )
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}