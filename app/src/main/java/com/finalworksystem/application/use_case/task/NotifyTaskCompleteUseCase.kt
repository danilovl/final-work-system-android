package com.finalworksystem.application.use_case.task

import com.finalworksystem.domain.task.repository.PutTaskRepository
import kotlinx.coroutines.flow.Flow

class NotifyTaskCompleteUseCase(private val taskRepository: PutTaskRepository) {
    operator fun invoke(taskId: Int, workId: Int): Flow<Result<Unit>> = taskRepository.notifyTaskComplete(taskId, workId)
}
