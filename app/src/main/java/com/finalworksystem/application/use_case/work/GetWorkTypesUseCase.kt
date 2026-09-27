package com.finalworksystem.application.use_case.work

import com.finalworksystem.domain.work.model.WorkType
import com.finalworksystem.domain.work.repository.GetWorkRepository
import kotlinx.coroutines.flow.Flow

class GetWorkTypesUseCase(private val workRepository: GetWorkRepository) {
    operator fun invoke(): Flow<Result<List<WorkType>>> {
        return workRepository.getWorkTypes()
    }
}
