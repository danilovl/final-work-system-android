package com.finalworksystem.data.task.repository

import com.finalworksystem.data.task.model.response.toPaginatedDomainModel
import com.finalworksystem.data.task.model.response.toDataModel
import com.finalworksystem.data.task.model.toDomainModel
import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.task.model.PaginatedTasks
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.task.repository.GetTaskRepository
import com.finalworksystem.infrastructure.api.ApiTaskService
import kotlinx.coroutines.flow.Flow

class GetTaskRepositoryImpl(private val apiTaskService: ApiTaskService) : GetTaskRepository {
    override fun getTasksForWork(workId: Int, page: Int?, limit: Int?): Flow<Result<PaginatedTasks>> = safeFlowResult {
        val response = apiTaskService.getTasksForWork(workId, page = page, limit = limit)

        response.handleResponse {taskListResponse ->
            taskListResponse.toPaginatedDomainModel()
        }
    }

    override fun getTasksForOwner(page: Int?, limit: Int?): Flow<Result<PaginatedTasks>> = safeFlowResult {
        val response = apiTaskService.getTasksForOwner(page = page, limit = limit)

        response.handleResponse {taskListResponse ->
            taskListResponse.toPaginatedDomainModel()
        }
    }

    override fun getTasksForSolver(page: Int?, limit: Int?): Flow<Result<PaginatedTasks>> = safeFlowResult {
        val response = apiTaskService.getTasksForSolver(page = page, limit = limit)

        response.handleResponse {taskListResponse ->
            taskListResponse.toPaginatedDomainModel()
        }
    }

    override fun getTaskDetail(workId: Int, taskId: Int): Flow<Result<Task>> = safeFlowResult {
        val response = apiTaskService.getTaskDetail(taskId, workId)

        response.handleResponse {taskDetailResponse ->
            val dataTask = taskDetailResponse.toDataModel()
            dataTask.toDomainModel()
        }
    }
}
