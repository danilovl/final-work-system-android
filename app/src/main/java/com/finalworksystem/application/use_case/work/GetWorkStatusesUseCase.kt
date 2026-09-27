package com.finalworksystem.application.use_case.work

import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.repository.GetWorkRepository
import kotlinx.coroutines.flow.Flow

class GetWorkStatusesUseCase(private val workRepository: GetWorkRepository) {
    operator fun invoke(): Flow<Result<List<WorkStatus>>> {
        return workRepository.getWorkStatuses()
    }
}
