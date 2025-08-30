package com.finalworksystem.application.use_case.task

import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.task.repository.GetTaskRepository
import kotlinx.coroutines.flow.Flow

class GetTaskDetailUseCase(private val taskRepository: GetTaskRepository) {
    operator fun invoke(workId: Int, taskId: Int): Flow<Result<Task>> = taskRepository.getTaskDetail(workId, taskId)
}
