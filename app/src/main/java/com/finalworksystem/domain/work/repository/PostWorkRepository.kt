package com.finalworksystem.domain.work.repository

import com.finalworksystem.data.work.model.request.WorkCreateRequest
import com.finalworksystem.domain.work.model.Work
import kotlinx.coroutines.flow.Flow

interface PostWorkRepository {
    fun postWorkCreate(request: WorkCreateRequest): Flow<Result<Work>>
}
