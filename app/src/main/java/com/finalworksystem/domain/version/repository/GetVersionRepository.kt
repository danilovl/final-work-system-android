package com.finalworksystem.domain.version.repository

import com.finalworksystem.domain.version.model.PaginatedVersions
import com.finalworksystem.domain.version.model.Version
import kotlinx.coroutines.flow.Flow

interface GetVersionRepository {
    fun getVersions(workId: Int): Flow<Result<List<Version>>>
    fun getVersionsWithPagination(workId: Int, page: Int, limit: Int): Flow<Result<PaginatedVersions>>
}