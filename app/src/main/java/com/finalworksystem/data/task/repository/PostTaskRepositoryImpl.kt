package com.finalworksystem.data.task.repository

import com.finalworksystem.data.task.model.request.TaskCreateRequest
import com.finalworksystem.data.task.model.response.toDataModel
import com.finalworksystem.data.task.model.toDomainModel
import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.task.repository.PostTaskRepository
import com.finalworksystem.infrastructure.api.ApiTaskService
import kotlinx.coroutines.flow.Flow

class PostTaskRepositoryImpl(private val apiTaskService: ApiTaskService) : PostTaskRepository {

    override fun postTaskCreate(request: TaskCreateRequest): Flow<Result<Task>> = safeFlowResult {
        val response = apiTaskService.postTaskCreate(request)

        response.handleResponse { taskResponse ->
            val dataTask = taskResponse.toDataModel()
            dataTask.toDomainModel()
        }
    }
}
