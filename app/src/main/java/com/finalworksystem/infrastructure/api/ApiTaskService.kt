package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.task.model.response.TaskListResponse
import com.finalworksystem.data.task.model.response.TaskResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiTaskService {
    @GET(ApiConstant.API_KEY_TASK_WORK_LIST)
    @Headers("X-Api-Service: ApiTaskService")
    suspend fun getTasksForWork(
        @Path("id") workId: Int,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<TaskListResponse>

    @GET(ApiConstant.API_KEY_TASK_LIST_OWNER)
    @Headers("X-Api-Service: ApiTaskService")
    suspend fun getTasksForOwner(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<TaskListResponse>

    @GET(ApiConstant.API_KEY_TASK_LIST_SOLVER)
    @Headers("X-Api-Service: ApiTaskService")
    suspend fun getTasksForSolver(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<TaskListResponse>

    @GET(ApiConstant.API_KEY_TASK_WORK_DETAIL)
    @Headers("X-Api-Service: ApiTaskService")
    suspend fun getTaskDetail(
        @Path("id_task") taskId: Int,
        @Path("id_work") workId: Int
    ): Response<TaskResponse>

    @PUT(ApiConstant.API_KEY_TASK_WORK_CHANGE_STATUS)
    @Headers("X-Api-Service: ApiTaskService")
    suspend fun changeTaskStatus(
        @Path("id_task") taskId: Int,
        @Path("id_work") workId: Int,
        @Path("type") type: String
    ): Response<Unit>

    @PUT(ApiConstant.API_KEY_TASK_WORK_NOTIFY_COMPLETE)
    @Headers("X-Api-Service: ApiTaskService")
    suspend fun notifyTaskComplete(
        @Path("id_task") taskId: Int,
        @Path("id_work") workId: Int
    ): Response<Unit>

    @DELETE(ApiConstant.API_KEY_TASK_WORK_DELETE)
    @Headers("X-Api-Service: ApiTaskService")
    suspend fun deleteTask(
        @Path("id_task") taskId: Int,
        @Path("id_work") workId: Int
    ): Response<Unit>
}
