package com.finalworksystem.data.event.repository

import com.finalworksystem.data.event.model.response.toDataModel
import com.finalworksystem.data.event.model.toDataModel
import com.finalworksystem.data.event.model.toDomainModel
import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.event.model.Event
import com.finalworksystem.domain.event.repository.GetEventRepository
import com.finalworksystem.infrastructure.api.ApiEventService
import kotlinx.coroutines.flow.Flow

class GetEventRepositoryImpl(private val apiEventService: ApiEventService) : GetEventRepository {

    override fun getEventsForWork(workId: Int): Flow<Result<List<Event>>> = safeFlowResult {
        val response = apiEventService.getEventsForWork(workId)

        response.handleResponse { eventListResponse ->
            val events = eventListResponse.toDataModel()
            events.map { dataEvent -> dataEvent.toDomainModel() }
        }
    }

    override fun getEvent(eventId: Int): Flow<Result<Event>> = safeFlowResult {
        val response = apiEventService.getEvent(eventId)

        response.handleResponse { eventResponse ->
            eventResponse.toDataModel().toDomainModel()
        }
    }
}
