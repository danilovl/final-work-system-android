package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.work.model.request.WorkCreateRequest
import com.finalworksystem.data.work.model.response.StatusResponse
import com.finalworksystem.data.work.model.response.TypeResponse
import com.finalworksystem.data.work.model.response.WorkCategoryDto
import com.finalworksystem.data.work.model.response.WorkDetailResponse
import com.finalworksystem.data.work.model.response.WorkListResponse
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.domain.work.model.WorkListType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiWorkService {
    @GET(ApiConstant.API_KEY_WORK_LIST)
    @Headers("X-Api-Service: ApiWorkService")
    suspend fun getWorks(
        @Path("type") type: WorkListType,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("search") search: String? = null
    ): Response<WorkListResponse>

    @GET(ApiConstant.API_KEY_WORK_DETAIL)
    @Headers("X-Api-Service: ApiWorkService")
    suspend fun getWorkDetail(
        @Path("id") id: Int,
        @Query("type") type: String? = null
    ): Response<WorkDetailResponse>

    @POST(ApiConstant.API_KEY_WORK_CREATE)
    @Headers("X-Api-Service: ApiWorkService")
    suspend fun postWorkCreate(
        @Body request: WorkCreateRequest
    ): Response<WorkResponse>

    @GET(ApiConstant.API_KEY_WORK_TYPES)
    @Headers("X-Api-Service: ApiWorkService")
    suspend fun getWorkTypes(): Response<List<TypeResponse>>

    @GET(ApiConstant.API_KEY_WORK_STATUSES)
    @Headers("X-Api-Service: ApiWorkService")
    suspend fun getWorkStatuses(): Response<List<StatusResponse>>

    @GET(ApiConstant.API_KEY_WORK_CATEGORIES)
    @Headers("X-Api-Service: ApiWorkService")
    suspend fun getWorkCategories(): Response<List<WorkCategoryDto>>
}
