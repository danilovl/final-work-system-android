package com.finalworksystem.application.use_case.event

import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.domain.event.repository.GetEventRepository
import kotlinx.coroutines.flow.Flow

class GetEventByIdUseCase(private val getEventRepository: GetEventRepository) {
    operator fun invoke(eventId: Int): Flow<Result<Event>> = getEventRepository.getEvent(eventId)
}
