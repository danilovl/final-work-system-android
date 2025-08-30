package com.finalworksystem.application.use_case.event_calendar

import com.finalworksystem.domain.event_calendar.model.EventCalendarManageCreateData
import com.finalworksystem.domain.event_calendar.repository.GetEventCalendarRepository
import kotlinx.coroutines.flow.Flow

class GetEventCalendarManageCreateDataUseCase(private val eventRepository: GetEventCalendarRepository) {
    operator fun invoke(): Flow<Result<EventCalendarManageCreateData>> = 
        eventRepository.getEventCalendarManageCreateData()
}
