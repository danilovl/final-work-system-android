package com.finalworksystem.application.use_case.work

import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.domain.work.repository.GetWorkRepository
import kotlinx.coroutines.flow.Flow

class GetWorkListUseCase(private val workRepository: GetWorkRepository) {
    operator fun invoke(type: WorkListType = WorkListType.AUTHOR): Flow<Result<List<Work>>> {
        return workRepository.getWorks(type)
    }
}
