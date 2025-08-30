package com.finalworksystem.application.use_case.task

import com.finalworksystem.domain.task.model.PaginatedTasks
import com.finalworksystem.domain.task.repository.GetTaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksForSolverUseCase(private val taskRepository: GetTaskRepository) {
    operator fun invoke(page: Int? = null, limit: Int? = null): Flow<Result<PaginatedTasks>> = 
        taskRepository.getTasksForSolver(page, limit)
}
