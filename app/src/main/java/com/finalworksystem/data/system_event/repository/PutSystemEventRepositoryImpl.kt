package com.finalworksystem.data.system_event.repository

import com.finalworksystem.data.util.ApiResponseUtil
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.system_event.repository.PutSystemEventRepository
import com.finalworksystem.infrastructure.api.ApiSystemEventService
import kotlinx.coroutines.flow.Flow

class PutSystemEventRepositoryImpl(private val apiSystemEventService: ApiSystemEventService) : PutSystemEventRepository {

    override fun changeViewed(eventId: Int): Flow<Result<Boolean>> = safeFlowResult {
        val response = apiSystemEventService.changeSystemEventViewed(
            id = eventId
        )

        val result = ApiResponseUtil.handleApiResponse(response).map { true }
        result
    }

    override fun changeAllViewed(): Flow<Result<Boolean>> = safeFlowResult {
        val response = apiSystemEventService.changeAllSystemEventsViewed()

        val result = ApiResponseUtil.handleApiResponse(response).map { true }
        result
    }
}
