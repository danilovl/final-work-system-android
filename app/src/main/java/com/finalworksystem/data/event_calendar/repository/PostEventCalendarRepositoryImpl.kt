package com.finalworksystem.data.event_calendar.repository

import com.finalworksystem.data.event_calendar.model.request.EventCalendarCreateRequest
import com.finalworksystem.data.event_calendar.model.response.EventCalendarCreateResponse
import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.data.util.handleSimpleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.event_calendar.repository.PostEventCalendarRepository
import com.finalworksystem.infrastructure.api.ApiEventService
import kotlinx.coroutines.flow.Flow

class PostEventCalendarRepositoryImpl(private val apiEventService: ApiEventService) : PostEventCalendarRepository {
    override fun postEventCalendarReservation(eventId: Int, workId: Int): Flow<Result<Unit>> = safeFlowResult {
        val response = apiEventService.postEventCalendarReservation(eventId, workId)

        response.handleSimpleResponse()
    }

    override fun postEventCalendarCreate(request: EventCalendarCreateRequest): Flow<Result<EventCalendarCreateResponse>> = safeFlowResult {
        val response = apiEventService.postEventCalendarCreate(request)

        response.handleResponse { it }
    }
}
