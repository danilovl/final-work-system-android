package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.event.model.response.EventListResponse
import com.finalworksystem.data.event.model.response.EventResponse
import com.finalworksystem.data.event_calendar.model.request.EventCalendarCreateRequest
import com.finalworksystem.data.event_calendar.model.response.EventCalendarCreateResponse
import com.finalworksystem.data.event_calendar.model.response.EventCalendarDto
import com.finalworksystem.data.event_calendar.model.response.EventCalendarManageCreateDataDto
import com.finalworksystem.data.work.model.response.WorkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiEventService {
    @GET(ApiConstant.API_KEY_EVENT_WORK_LIST)
    @Headers("X-Api-Service: ApiEventService")
    suspend fun getEventsForWork(
        @Path("id") workId: Int
    ): Response<EventListResponse>

    @GET(ApiConstant.API_KEY_EVENT_CALENDAR_LIST)
    @Headers("X-Api-Service: ApiEventService")
    suspend fun getEventCalendar(
        @Path("type") type: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<EventCalendarDto>>

    @GET(ApiConstant.API_KEY_EVENT_CALENDAR_USER_WORKS)
    @Headers("X-Api-Service: ApiEventService")
    suspend fun getEventCalendarUserWorks(): Response<List<WorkResponse>>

    @POST(ApiConstant.API_KEY_EVENT_CALENDAR_RESERVATION)
    @Headers("X-Api-Service: ApiEventService")
    suspend fun postEventCalendarReservation(
        @Path("id_event") eventId: Int,
        @Path("id_work") workId: Int
    ): Response<Unit>

    @GET(ApiConstant.API_KEY_EVENT_CALENDAR_MANAGE_CREATE_DATA)
    @Headers("X-Api-Service: ApiEventService")
    suspend fun getEventCalendarManageCreateData(): Response<EventCalendarManageCreateDataDto>

    @POST(ApiConstant.API_KEY_EVENT_CALENDAR_CREATE)
    @Headers("X-Api-Service: ApiEventService")
    suspend fun postEventCalendarCreate(
        @Body request: EventCalendarCreateRequest
    ): Response<EventCalendarCreateResponse>

    @GET(ApiConstant.API_KEY_EVENT_GET)
    @Headers("X-Api-Service: ApiEventService")
    suspend fun getEvent(
        @Path("id") eventId: Int
    ): Response<EventResponse>

    @DELETE(ApiConstant.API_KEY_EVENT_DELETE)
    @Headers("X-Api-Service: ApiEventService")
    suspend fun deleteEvent(
        @Path("id") eventId: Int
    ): Response<Unit>
}
