package com.finalworksystem.application.use_case.event_calendar

import com.finalworksystem.domain.event_calendar.repository.GetEventCalendarRepository
import com.finalworksystem.domain.work.model.Work
import kotlinx.coroutines.flow.Flow

class GetEventCalendarUserWorksUseCase(private val eventRepository: GetEventCalendarRepository) {
    operator fun invoke(): Flow<Result<List<Work>>> = eventRepository.getEventCalendarUserWorks()
}
