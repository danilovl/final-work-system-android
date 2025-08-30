package com.finalworksystem.domain.task.repository

import com.finalworksystem.domain.task.model.TaskStatus
import kotlinx.coroutines.flow.Flow

interface PutTaskRepository {
    fun changeTaskStatus(taskId: Int, workId: Int, status: TaskStatus): Flow<Result<Unit>>
    fun notifyTaskComplete(taskId: Int, workId: Int): Flow<Result<Unit>>
}