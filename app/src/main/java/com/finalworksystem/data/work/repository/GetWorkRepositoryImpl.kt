package com.finalworksystem.data.work.repository

import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.data.work.model.response.toDataModel
import com.finalworksystem.data.work.model.response.toDomainModel
import com.finalworksystem.data.work.model.toDomainModel
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.work.model.PaginatedWorks
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkCategory
import com.finalworksystem.domain.work.model.WorkFilter
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.model.WorkType
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

    override fun getWorksWithPagination(type: WorkListType, page: Int, limit: Int, filter: WorkFilter?): Flow<Result<PaginatedWorks>> = safeFlowResult {
        val response = apiWorkService.getWorks(
            type = type,
            page = page,
            limit = limit,
            title = filter?.title,
            shortcut = filter?.shortcut,
            statusIds = filter?.statusIds,
            workTypeId = filter?.typeId,
            authorId = filter?.authorId,
            opponentId = filter?.opponentId,
            deadline = filter?.deadline
        )

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

    override fun getWorkTypes(): Flow<Result<List<WorkType>>> = safeFlowResult {
        val response = apiWorkService.getWorkTypes()

        response.handleResponse { types ->
            types.map { it.toDataModel() }
        }
    }

    override fun getWorkStatuses(): Flow<Result<List<WorkStatus>>> = safeFlowResult {
        val response = apiWorkService.getWorkStatuses()

        response.handleResponse { statuses ->
            statuses.map { it.toDataModel() }
        }
    }

    override fun getWorkCategories(): Flow<Result<List<WorkCategory>>> = safeFlowResult {
        val response = apiWorkService.getWorkCategories()

        response.handleResponse { categories ->
            categories.map { it.toDomainModel() }
        }
    }
}
