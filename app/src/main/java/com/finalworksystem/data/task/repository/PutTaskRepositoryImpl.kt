package com.finalworksystem.data.task.repository

import com.finalworksystem.data.util.ApiResponseUtil
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.task.model.TaskStatus
import com.finalworksystem.domain.task.repository.PutTaskRepository
import com.finalworksystem.infrastructure.api.ApiTaskService
import kotlinx.coroutines.flow.Flow

class PutTaskRepositoryImpl(private val apiTaskService: ApiTaskService) : PutTaskRepository {
    override fun changeTaskStatus(taskId: Int, workId: Int, status: TaskStatus): Flow<Result<Unit>> = safeFlowResult {
        val response = apiTaskService.changeTaskStatus(taskId, workId, status.value)

        ApiResponseUtil.handleApiResponse(response)
    }

    override fun notifyTaskComplete(taskId: Int, workId: Int): Flow<Result<Unit>> = safeFlowResult {
        val response = apiTaskService.notifyTaskComplete(taskId, workId)

        ApiResponseUtil.handleApiResponse(response)
    }
}
