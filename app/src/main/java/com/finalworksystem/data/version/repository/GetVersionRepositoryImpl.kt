package com.finalworksystem.data.version.repository

import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.data.version.model.response.toDataModel
import com.finalworksystem.data.version.model.toDomainModel
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.version.model.PaginatedVersions
import com.finalworksystem.domain.version.model.Version
import com.finalworksystem.domain.version.repository.GetVersionRepository
import com.finalworksystem.infrastructure.api.ApiVersionService
import kotlinx.coroutines.flow.Flow

class GetVersionRepositoryImpl(
    private val apiVersionService: ApiVersionService
) : GetVersionRepository {

    override fun getVersions(workId: Int): Flow<Result<List<Version>>> = safeFlowResult {
        val response = apiVersionService.getVersions(workId)

        val result = response.handleResponse { versionListResponse ->
            val versions = versionListResponse.toDataModel()
            versions.map { dataVersion -> dataVersion.toDomainModel() }
        }

        result
    }

    override fun getVersionsWithPagination(workId: Int, page: Int, limit: Int): Flow<Result<PaginatedVersions>> = safeFlowResult {
        val response = apiVersionService.getVersions(workId, page, limit)

        val result = response.handleResponse { versionListResponse ->
            if (versionListResponse.success) {
                val versions = versionListResponse.toDataModel()
                val domainVersions = versions.map { dataVersion -> dataVersion.toDomainModel() }

                PaginatedVersions(
                    versions = domainVersions,
                    totalCount = versionListResponse.totalCount,
                    currentItemCount = versionListResponse.count,
                    numItemsPerPage = limit
                )
            } else {
                throw Exception("Failed to fetch versions")
            }
        }

        result
    }
}
