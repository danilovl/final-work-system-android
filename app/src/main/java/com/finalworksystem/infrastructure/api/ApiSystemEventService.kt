package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.system_event.model.response.SystemEventListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiSystemEventService {
    @GET(ApiConstant.API_KEY_SYSTEM_EVENT_LIST)
    @Headers("X-Api-Service: ApiSystemEventService")
    suspend fun getSystemEvents(
        @Path("type") type: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<SystemEventListResponse>

    @PUT(ApiConstant.API_KEY_SYSTEM_EVENT_CHANGE_VIEWED)
    @Headers("X-Api-Service: ApiSystemEventService")
    suspend fun changeSystemEventViewed(
        @Path("id") id: Int
    ): Response<Void>

    @PUT(ApiConstant.API_KEY_SYSTEM_EVENT_ALL_CHANGE_VIEWED)
    @Headers("X-Api-Service: ApiSystemEventService")
    suspend fun changeAllSystemEventsViewed(): Response<Void>
}
