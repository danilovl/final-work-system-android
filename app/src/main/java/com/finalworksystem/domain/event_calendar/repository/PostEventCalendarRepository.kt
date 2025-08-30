package com.finalworksystem.domain.event_calendar.repository

import com.finalworksystem.data.event_calendar.model.request.EventCalendarCreateRequest
import com.finalworksystem.data.event_calendar.model.response.EventCalendarCreateResponse
import kotlinx.coroutines.flow.Flow

interface PostEventCalendarRepository {
    fun postEventCalendarReservation(eventId: Int, workId: Int): Flow<Result<Unit>>
    fun postEventCalendarCreate(request: EventCalendarCreateRequest): Flow<Result<EventCalendarCreateResponse>>
}