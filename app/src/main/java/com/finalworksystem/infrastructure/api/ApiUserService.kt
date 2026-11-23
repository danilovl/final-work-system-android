package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.user.model.request.ChangePasswordRequest
import com.finalworksystem.data.user.model.response.UserDetailResponse
import com.finalworksystem.data.user.model.response.UserListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiUserService {
    @GET(ApiConstant.API_KEY_USER_DETAIL)
    suspend fun getUserDetail(): Response<UserDetailResponse>

    @GET(ApiConstant.API_KEY_USER_LIST)
    suspend fun getUserList(
        @Path("type") type: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null
    ): Response<UserListResponse>

    @PUT(ApiConstant.API_KEY_USER_CHANGE_PASSWORD)
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<Unit>

    @GET(ApiConstant.API_KEY_USER_SEARCH)
    suspend fun searchUsers(
        @Path("type") type: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("search") search: String? = null
    ): Response<UserListResponse>
}
