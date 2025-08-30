package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.user.model.response.UserDetailResponse
import com.finalworksystem.data.user.model.response.UserListResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiUserService {
    @GET(ApiConstant.API_KEY_USER_DETAIL)
    @Headers("X-Api-Service: ApiUserService")
    suspend fun getUserDetail(): Response<UserDetailResponse>

    @GET(ApiConstant.API_KEY_USER_LIST)
    @Headers("X-Api-Service: ApiUserService")
    suspend fun getUserList(
        @Path("type") type: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<UserListResponse>

    @GET(ApiConstant.API_KEY_USER_PROFILE_IMAGE)
    @Headers("X-Api-Service: ApiUserService")
    suspend fun getUserProfileImage(@Path("id") userId: Int): Response<ResponseBody>
}
