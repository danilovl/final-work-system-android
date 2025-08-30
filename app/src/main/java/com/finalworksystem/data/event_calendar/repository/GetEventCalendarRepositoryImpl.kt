package com.finalworksystem.data.event_calendar.repository

import com.finalworksystem.data.event_calendar.model.response.toDomainModel
import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.data.work.model.response.toDomainModel
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.event_calendar.model.CalendarEvent
import com.finalworksystem.domain.event_calendar.model.EventCalendarManageCreateData
import com.finalworksystem.domain.event_calendar.repository.GetEventCalendarRepository
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.infrastructure.api.ApiEventService
import kotlinx.coroutines.flow.Flow

class GetEventCalendarRepositoryImpl(private val apiEventService: ApiEventService) : GetEventCalendarRepository {
    override fun getEventCalendar(type: String, start: String, end: String): Flow<Result<List<CalendarEvent>>> = safeFlowResult {
        val response = apiEventService.getEventCalendar(type, start, end)

        response.handleResponse { calendarEventsDto ->
            calendarEventsDto.map { it.toDomainModel() }
        }
    }

    override fun getEventCalendarUserWorks(): Flow<Result<List<Work>>> = safeFlowResult {
        val response = apiEventService.getEventCalendarUserWorks()

        response.handleResponse { workResponseList ->
            workResponseList.map { it.toDomainModel() }
        }
    }

    override fun getEventCalendarManageCreateData(): Flow<Result<EventCalendarManageCreateData>> = safeFlowResult {
        val response = apiEventService.getEventCalendarManageCreateData()

        response.handleResponse { createDataDto ->
            createDataDto.toDomainModel()
        }
    }
}
