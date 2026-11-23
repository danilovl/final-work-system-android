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
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiWorkService {
    @GET(ApiConstant.API_KEY_WORK_LIST)
    suspend fun getWorks(
        @Path("type") type: WorkListType,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("title") title: String? = null,
        @Query("shortcut") shortcut: String? = null,
        @Query("status[]") statusIds: List<Int>? = null,
        @Query("type") workTypeId: Int? = null,
        @Query("author") authorId: Int? = null,
        @Query("opponent") opponentId: Int? = null,
        @Query("deadline") deadline: String? = null
    ): Response<WorkListResponse>

    @GET(ApiConstant.API_KEY_WORK_DETAIL)
    suspend fun getWorkDetail(
        @Path("id") id: Int,
        @Query("type") type: String? = null
    ): Response<WorkDetailResponse>

    @POST(ApiConstant.API_KEY_WORK_CREATE)
    suspend fun postWorkCreate(
        @Body request: WorkCreateRequest
    ): Response<WorkResponse>

    @GET(ApiConstant.API_KEY_WORK_TYPES)
    suspend fun getWorkTypes(): Response<List<TypeResponse>>

    @GET(ApiConstant.API_KEY_WORK_STATUSES)
    suspend fun getWorkStatuses(): Response<List<StatusResponse>>

    @GET(ApiConstant.API_KEY_WORK_CATEGORIES)
    suspend fun getWorkCategories(): Response<List<WorkCategoryDto>>
}
