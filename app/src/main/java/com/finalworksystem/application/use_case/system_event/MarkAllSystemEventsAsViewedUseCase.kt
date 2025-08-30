package com.finalworksystem.application.use_case.system_event

import com.finalworksystem.domain.system_event.repository.PutSystemEventRepository
import kotlinx.coroutines.flow.Flow

class MarkAllSystemEventsAsViewedUseCase(private val systemEventRepository: PutSystemEventRepository) {
    operator fun invoke(): Flow<Result<Boolean>> {
        return systemEventRepository.changeAllViewed()
    }
}
