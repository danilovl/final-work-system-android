package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.version.model.response.VersionListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiVersionService {
    @GET(ApiConstant.API_KEY_VERSION_WORK_LIST)
    @Headers("X-Api-Service: ApiVersionService")
    suspend fun getVersions(
        @Path("id") workId: Int,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<VersionListResponse>
}
