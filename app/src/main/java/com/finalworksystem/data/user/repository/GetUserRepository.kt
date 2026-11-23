package com.finalworksystem.data.user.repository

import com.finalworksystem.data.user.model.response.toDataModel
import com.finalworksystem.data.user.model.response.toDomainModel
import com.finalworksystem.data.user.model.toDomainModel
import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserListResult
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.user.repository.GetUserRepository
import com.finalworksystem.infrastructure.api.ApiUserService
import com.finalworksystem.infrastructure.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetUserRepository(
    private val sessionManager: SessionManager,
    private val apiUserService: ApiUserService
) : GetUserRepository {

    override fun getUserDetail(): Flow<Result<User>> = safeFlowResult {
        val response = apiUserService.getUserDetail()

        val result = response.handleResponse { userDetailResponse ->
            val safeResponse = if (userDetailResponse.roles == null) {
                userDetailResponse.copy(roles = emptyList())
            } else {
                userDetailResponse
            }

            val dataUser = safeResponse.toDataModel()
            dataUser.toDomainModel()
        }

        if (result.isSuccess) {
            val domainUser = result.getOrThrow()
            withContext(Dispatchers.IO) {
                val existingToken = sessionManager.getAuthData()?.token
                val userWithToken = if (existingToken != null) {
                    domainUser.copy(token = existingToken)
                } else {
                    domainUser
                }
                sessionManager.saveUser(userWithToken)
            }
        }

        result
    }

    override fun getUserList(
        type: UserType,
        page: Int,
        pageSize: Int,
        search: String?
    ): Flow<Result<UserListResult>> = safeFlowResult {
        val response = if (!search.isNullOrBlank()) {
            apiUserService.searchUsers(type.value, page, pageSize, search)
        } else {
            apiUserService.getUserList(type.value, page, pageSize, search)
        }

        val result = response.handleResponse { userListResponse ->
            userListResponse.toDomainModel()
        }
        result
    }

}
