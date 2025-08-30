package com.finalworksystem.application.use_case.work

import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.repository.GetWorkRepository
import kotlinx.coroutines.flow.Flow

class GetWorkDetailUseCase(private val workRepository: GetWorkRepository) {
    operator fun invoke(id: Int, type: String? = null): Flow<Result<Work>> {
        return workRepository.getWorkDetail(id, type)
    }
}
