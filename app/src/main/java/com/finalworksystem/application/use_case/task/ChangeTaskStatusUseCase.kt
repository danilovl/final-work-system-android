package com.finalworksystem.application.use_case.task

import com.finalworksystem.domain.task.model.TaskStatus
import com.finalworksystem.domain.task.repository.PutTaskRepository
import kotlinx.coroutines.flow.Flow

class ChangeTaskStatusUseCase(private val taskRepository: PutTaskRepository) {
    operator fun invoke(taskId: Int, workId: Int, status: TaskStatus): Flow<Result<Unit>> = 
        taskRepository.changeTaskStatus(taskId, workId, status)
}
