package com.finalworksystem.application.use_case.event_calendar

import com.finalworksystem.domain.event_calendar.model.CalendarEvent
import com.finalworksystem.domain.event_calendar.repository.GetEventCalendarRepository
import kotlinx.coroutines.flow.Flow

class GetEventCalendarUseCase(private val eventRepository: GetEventCalendarRepository) {
    operator fun invoke(type: String, start: String, end: String): Flow<Result<List<CalendarEvent>>> = 
        eventRepository.getEventCalendar(type, start, end)
}
