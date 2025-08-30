package com.finalworksystem.application.use_case.version

import com.finalworksystem.domain.version.model.Version
import com.finalworksystem.domain.version.repository.GetVersionRepository
import kotlinx.coroutines.flow.Flow

class GetVersionsForWorkUseCase(private val getVersionRepository: GetVersionRepository) {
    operator fun invoke(workId: Int): Flow<Result<List<Version>>> {
        return getVersionRepository.getVersions(workId)
    }
}
