package com.finalworksystem.domain.user.repository

import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserListResult
import com.finalworksystem.domain.user.model.UserProfileImage
import com.finalworksystem.domain.user.model.UserType
import kotlinx.coroutines.flow.Flow

interface GetUserRepository {
    fun getUserDetail(): Flow<Result<User>>
    fun getUserList(type: UserType, page: Int = 1, pageSize: Int = 20): Flow<Result<UserListResult>>
    fun getUserProfileImage(userId: Int): Flow<Result<UserProfileImage>>
}