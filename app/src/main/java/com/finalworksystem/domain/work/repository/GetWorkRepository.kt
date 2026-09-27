package com.finalworksystem.domain.work.repository

import com.finalworksystem.domain.work.model.PaginatedWorks
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkCategory
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.model.WorkType
import kotlinx.coroutines.flow.Flow

interface GetWorkRepository {
    fun getWorks(type: WorkListType): Flow<Result<List<Work>>>
    fun getWorksWithPagination(type: WorkListType, page: Int, limit: Int, search: String? = null): Flow<Result<PaginatedWorks>>
    fun getWorkDetail(id: Int, type: String? = null): Flow<Result<Work>>
    fun getWorkTypes(): Flow<Result<List<WorkType>>>
    fun getWorkStatuses(): Flow<Result<List<WorkStatus>>>
    fun getWorkCategories(): Flow<Result<List<WorkCategory>>>
}