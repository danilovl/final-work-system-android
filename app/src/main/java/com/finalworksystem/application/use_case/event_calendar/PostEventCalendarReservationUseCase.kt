package com.finalworksystem.application.use_case.event_calendar

import com.finalworksystem.domain.event_calendar.repository.PostEventCalendarRepository
import kotlinx.coroutines.flow.Flow

class PostEventCalendarReservationUseCase(private val eventRepository: PostEventCalendarRepository) {
    operator fun invoke(eventId: Int, workId: Int): Flow<Result<Unit>> = 
        eventRepository.postEventCalendarReservation(eventId, workId)
}
