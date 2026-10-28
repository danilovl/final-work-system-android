package com.finalworksystem.application.use_case.task

import com.finalworksystem.data.task.model.request.TaskCreateRequest
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.task.repository.PostTaskRepository
import kotlinx.coroutines.flow.Flow

class PostTaskCreateUseCase(private val postTaskRepository: PostTaskRepository) {
    operator fun invoke(request: TaskCreateRequest): Flow<Result<Task>> {
        return postTaskRepository.postTaskCreate(request)
    }
}
