package com.finalworksystem.application.use_case.task

import com.finalworksystem.domain.task.model.PaginatedTasks
import com.finalworksystem.domain.task.repository.GetTaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksForWorkUseCase(private val taskRepository: GetTaskRepository) {
    operator fun invoke(workId: Int, page: Int? = null, limit: Int? = null): Flow<Result<PaginatedTasks>> = 
        taskRepository.getTasksForWork(workId, page, limit)
}
