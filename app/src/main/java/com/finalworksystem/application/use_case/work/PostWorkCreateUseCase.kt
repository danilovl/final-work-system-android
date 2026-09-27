package com.finalworksystem.application.use_case.work

import com.finalworksystem.data.work.model.request.WorkCreateRequest
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.repository.PostWorkRepository
import kotlinx.coroutines.flow.Flow

class PostWorkCreateUseCase(private val postWorkRepository: PostWorkRepository) {
    operator fun invoke(request: WorkCreateRequest): Flow<Result<Work>> {
        return postWorkRepository.postWorkCreate(request)
    }
}
