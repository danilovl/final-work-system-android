package com.finalworksystem.data.task.repository

import com.finalworksystem.data.util.ApiResponseUtil
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.task.repository.DeleteTaskRepository
import com.finalworksystem.infrastructure.api.ApiTaskService
import kotlinx.coroutines.flow.Flow

class DeleteTaskRepositoryImpl(private val apiTaskService: ApiTaskService) : DeleteTaskRepository {
    override fun deleteTask(taskId: Int, workId: Int): Flow<Result<Unit>> = safeFlowResult {
        val response = apiTaskService.deleteTask(taskId, workId)

        ApiResponseUtil.handleApiResponse(response)
    }
}
