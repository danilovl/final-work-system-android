package com.finalworksystem.data.system_event.repository

import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.system_event.model.SystemEventTypeEnum
import com.finalworksystem.domain.system_event.model.toDomainList
import com.finalworksystem.domain.system_event.repository.GetSystemEventRepository
import com.finalworksystem.infrastructure.api.ApiConstant
import com.finalworksystem.infrastructure.api.ApiSystemEventService
import kotlinx.coroutines.flow.Flow
import com.finalworksystem.domain.system_event.model.SystemEvent as DomainSystemEvent

class GetSystemEventRepositoryImpl(private val apiSystemEventService: ApiSystemEventService) : GetSystemEventRepository {

    override fun getSystemEvents(
        type: SystemEventTypeEnum,
        page: Int?,
        limit: Int?
    ): Flow<Result<List<DomainSystemEvent>>> = safeFlowResult {
        val typeString = when(type) {
            SystemEventTypeEnum.UNREAD -> ApiConstant.SystemEventType.UNREAD
            SystemEventTypeEnum.READ -> ApiConstant.SystemEventType.READ
            SystemEventTypeEnum.ALL -> ApiConstant.SystemEventType.ALL
        }

        val response = apiSystemEventService.getSystemEvents(
            type = typeString,
            page = page,
            limit = limit
        )

        val result = response.handleResponse { eventListResponse ->
            if (eventListResponse.success) {
                eventListResponse.toDomainList()
            } else {
                throw Exception("Failed to fetch system events")
            }
        }

        result
    }

    override fun getSystemEventsWithPagination(
        type: SystemEventTypeEnum,
        page: Int,
        limit: Int
    ): Flow<Result<Triple<List<DomainSystemEvent>, Boolean, Int>>> = safeFlowResult {
        val typeString = when(type) {
            SystemEventTypeEnum.UNREAD -> ApiConstant.SystemEventType.UNREAD
            SystemEventTypeEnum.READ -> ApiConstant.SystemEventType.READ
            SystemEventTypeEnum.ALL -> ApiConstant.SystemEventType.ALL
        }

        val response = apiSystemEventService.getSystemEvents(
            type = typeString,
            page = page,
            limit = limit
        )

        val result = response.handleResponse { eventListResponse ->
            if (eventListResponse.success) {
                val domainEvents = eventListResponse.toDomainList()
                val hasMoreEvents = eventListResponse.count + ((page - 1) * limit) < eventListResponse.totalCount
                Triple(domainEvents, hasMoreEvents, eventListResponse.totalCount)
            } else {
                throw Exception("Failed to fetch system events")
            }
        }

        result
    }
}
