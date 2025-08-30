package com.finalworksystem.application.use_case.version

import com.finalworksystem.domain.version.model.PaginatedVersions
import com.finalworksystem.domain.version.repository.GetVersionRepository
import kotlinx.coroutines.flow.Flow

class GetVersionsForWorkWithPaginationUseCase(private val getVersionRepository: GetVersionRepository) {
    operator fun invoke(workId: Int, page: Int, limit: Int): Flow<Result<PaginatedVersions>> {
        return getVersionRepository.getVersionsWithPagination(workId, page, limit)
    }
}
