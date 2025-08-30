package com.finalworksystem.data.work.repository

import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.data.work.model.response.toDataModel
import com.finalworksystem.data.work.model.toDomainModel
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.work.model.PaginatedWorks
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.domain.work.repository.GetWorkRepository
import com.finalworksystem.infrastructure.api.ApiWorkService
import kotlinx.coroutines.flow.Flow

class GetWorkRepositoryImpl(private val apiWorkService: ApiWorkService) : GetWorkRepository {

    override fun getWorks(type: WorkListType): Flow<Result<List<Work>>> = safeFlowResult {
        val response = apiWorkService.getWorks(type)

        response.handleResponse { workListResponse ->
            val works = workListResponse.toDataModel()
            works.map { dataWork -> dataWork.toDomainModel() }
        }
    }

    override fun getWorksWithPagination(type: WorkListType, page: Int, limit: Int, search: String?): Flow<Result<PaginatedWorks>> = safeFlowResult {
        val response = apiWorkService.getWorks(type, page, limit, search)

        response.handleResponse { workListResponse ->
            if (workListResponse.success) {
                val works = workListResponse.toDataModel()
                val domainWorks = works.map { dataWork -> dataWork.toDomainModel() }

                PaginatedWorks(
                    works = domainWorks,
                    totalCount = workListResponse.totalCount,
                    currentItemCount = workListResponse.count,
                    numItemsPerPage = limit
                )
            } else {
                throw Exception("Failed to fetch works")
            }
        }
    }

    override fun getWorkDetail(id: Int, type: String?): Flow<Result<Work>> = safeFlowResult {
        val response = apiWorkService.getWorkDetail(id, type)

        response.handleResponse { workResponse ->
            val dataWork = workResponse.toDataModel()
            dataWork.toDomainModel()
        }
    }
}
