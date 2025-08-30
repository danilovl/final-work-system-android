package com.finalworksystem.domain.system_event.repository

import com.finalworksystem.domain.system_event.model.SystemEvent
import com.finalworksystem.domain.system_event.model.SystemEventTypeEnum
import kotlinx.coroutines.flow.Flow

interface GetSystemEventRepository {
    fun getSystemEvents(
        type: SystemEventTypeEnum = SystemEventTypeEnum.UNREAD,
        page: Int? = null,
        limit: Int? = null
    ): Flow<Result<List<SystemEvent>>>

    fun getSystemEventsWithPagination(
        type: SystemEventTypeEnum = SystemEventTypeEnum.UNREAD,
        page: Int,
        limit: Int
    ): Flow<Result<Triple<List<SystemEvent>, Boolean, Int>>>
}