package com.finalworksystem.application.use_case.system_event

import com.finalworksystem.domain.system_event.model.SystemEvent
import com.finalworksystem.domain.system_event.model.SystemEventTypeEnum
import com.finalworksystem.domain.system_event.repository.GetSystemEventRepository
import kotlinx.coroutines.flow.Flow

class GetSystemEventsUseCase(private val systemEventRepository: GetSystemEventRepository) {
    operator fun invoke(type: SystemEventTypeEnum = SystemEventTypeEnum.UNREAD): Flow<Result<List<SystemEvent>>> {
        return systemEventRepository.getSystemEvents(type)
    }
}
