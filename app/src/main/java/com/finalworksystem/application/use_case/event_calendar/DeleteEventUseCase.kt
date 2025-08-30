package com.finalworksystem.application.use_case.event_calendar

import com.finalworksystem.domain.event_calendar.repository.DeleteEventCalendarRepository
import kotlinx.coroutines.flow.Flow

class DeleteEventUseCase(private val eventRepository: DeleteEventCalendarRepository) {
    operator fun invoke(eventId: Int): Flow<Result<Unit>> = eventRepository.deleteEvent(eventId)
}
