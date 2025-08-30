package com.finalworksystem.data.event_calendar.repository

import com.finalworksystem.data.util.handleSimpleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.event_calendar.repository.DeleteEventCalendarRepository
import com.finalworksystem.infrastructure.api.ApiEventService
import kotlinx.coroutines.flow.Flow

class DeleteEventCalendarRepositoryImpl(private val apiEventService: ApiEventService) : DeleteEventCalendarRepository {
    override fun deleteEvent(eventId: Int): Flow<Result<Unit>> = safeFlowResult {
        val response = apiEventService.deleteEvent(eventId)

        response.handleSimpleResponse()
    }
}
