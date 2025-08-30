package com.finalworksystem.domain.task.repository

import kotlinx.coroutines.flow.Flow

interface DeleteTaskRepository {
    fun deleteTask(taskId: Int, workId: Int): Flow<Result<Unit>>
}