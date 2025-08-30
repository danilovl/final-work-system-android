package com.finalworksystem.application.use_case.task

import com.finalworksystem.domain.task.repository.DeleteTaskRepository
import kotlinx.coroutines.flow.Flow

class DeleteTaskUseCase(private val taskRepository: DeleteTaskRepository) {
    operator fun invoke(taskId: Int, workId: Int): Flow<Result<Unit>> = taskRepository.deleteTask(taskId, workId)
}
