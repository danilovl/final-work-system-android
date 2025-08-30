package com.finalworksystem.application.use_case.work

import com.finalworksystem.domain.work.model.PaginatedWorks
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.domain.work.repository.GetWorkRepository
import kotlinx.coroutines.flow.Flow

class GetWorkListWithPaginationUseCase(private val workRepository: GetWorkRepository) {
    operator fun invoke(
        type: WorkListType = WorkListType.AUTHOR,
        page: Int,
        limit: Int,
        search: String? = null
    ): Flow<Result<PaginatedWorks>> {
        return workRepository.getWorksWithPagination(type, page, limit, search)
    }
}
