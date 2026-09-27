package com.finalworksystem.application.use_case.work

import com.finalworksystem.domain.work.model.WorkCategory
import com.finalworksystem.domain.work.repository.GetWorkRepository
import kotlinx.coroutines.flow.Flow

class GetWorkCategoriesUseCase(private val workRepository: GetWorkRepository) {
    operator fun invoke(): Flow<Result<List<WorkCategory>>> {
        return workRepository.getWorkCategories()
    }
}
