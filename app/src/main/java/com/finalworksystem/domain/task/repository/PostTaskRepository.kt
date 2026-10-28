package com.finalworksystem.domain.task.repository

import com.finalworksystem.data.task.model.request.TaskCreateRequest
import com.finalworksystem.domain.task.model.Task
import kotlinx.coroutines.flow.Flow

interface PostTaskRepository {
    fun postTaskCreate(request: TaskCreateRequest): Flow<Result<Task>>
}
