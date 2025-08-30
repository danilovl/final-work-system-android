package com.finalworksystem.application.use_case.event

import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.domain.event.repository.GetEventRepository
import kotlinx.coroutines.flow.Flow

class GetEventsForWorkUseCase(private val getEventRepository: GetEventRepository) {
    operator fun invoke(workId: Int): Flow<Result<List<Event>>> = getEventRepository.getEventsForWork(workId)
}
