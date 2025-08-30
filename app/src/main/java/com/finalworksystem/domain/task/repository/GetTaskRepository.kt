package com.finalworksystem.domain.task.repository

import com.finalworksystem.domain.task.model.PaginatedTasks
import com.finalworksystem.domain.task.model.Task
import kotlinx.coroutines.flow.Flow

interface GetTaskRepository {
    fun getTasksForWork(workId: Int, page: Int? = null, limit: Int? = null): Flow<Result<PaginatedTasks>>
    fun getTasksForOwner(page: Int? = null, limit: Int? = null): Flow<Result<PaginatedTasks>>
    fun getTasksForSolver(page: Int? = null, limit: Int? = null): Flow<Result<PaginatedTasks>>
    fun getTaskDetail(workId: Int, taskId: Int): Flow<Result<Task>>
}