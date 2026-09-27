package com.finalworksystem.data.work.repository

import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.data.work.model.request.WorkCreateRequest
import com.finalworksystem.data.work.model.response.toDataModel
import com.finalworksystem.data.work.model.toDomainModel
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.repository.PostWorkRepository
import com.finalworksystem.infrastructure.api.ApiWorkService
import kotlinx.coroutines.flow.Flow

class PostWorkRepositoryImpl(private val apiWorkService: ApiWorkService) : PostWorkRepository {

    override fun postWorkCreate(request: WorkCreateRequest): Flow<Result<Work>> = safeFlowResult {
        val response = apiWorkService.postWorkCreate(request)

        response.handleResponse { workResponse ->
            val dataWork = workResponse.toDataModel()
            dataWork.toDomainModel()
        }
    }
}
