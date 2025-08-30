package com.finalworksystem.application.use_case.user

import com.finalworksystem.domain.user.model.UserListResult
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.user.repository.GetUserRepository
import kotlinx.coroutines.flow.Flow

class GetUserListUseCase(private val userRepository: GetUserRepository) {
    operator fun invoke(type: UserType, page: Int = 1, pageSize: Int = 20): Flow<Result<UserListResult>> {
        return userRepository.getUserList(type, page, pageSize)
    }
}
