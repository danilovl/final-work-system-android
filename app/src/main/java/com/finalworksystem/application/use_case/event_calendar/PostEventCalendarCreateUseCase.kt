package com.finalworksystem.application.use_case.event_calendar

import com.finalworksystem.data.event_calendar.model.request.EventCalendarCreateRequest
import com.finalworksystem.data.event_calendar.model.response.EventCalendarCreateResponse
import com.finalworksystem.domain.event_calendar.repository.PostEventCalendarRepository
import kotlinx.coroutines.flow.Flow

class PostEventCalendarCreateUseCase(private val eventRepository: PostEventCalendarRepository) {
    operator fun invoke(request: EventCalendarCreateRequest): Flow<Result<EventCalendarCreateResponse>> = 
        eventRepository.postEventCalendarCreate(request)
}
