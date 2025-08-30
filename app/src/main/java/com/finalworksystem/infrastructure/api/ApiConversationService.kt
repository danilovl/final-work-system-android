package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.conversation.model.ConversationDto
import com.finalworksystem.data.conversation.model.request.CreateMessageRequestDto
import com.finalworksystem.data.conversation.model.response.ConversationListResponse
import com.finalworksystem.data.conversation.model.response.ConversationMessagesResponseDto
import com.finalworksystem.data.conversation.model.response.ConversationWorkResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiConversationService {
    @GET(ApiConstant.API_KEY_CONVERSATION_LIST)
    @Headers("X-Api-Service: ApiConversationService")
    suspend fun getConversations(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("search") search: String? = null
    ): Response<ConversationListResponse>

    @GET(ApiConstant.API_KEY_CONVERSATION_DETAIL)
    @Headers("X-Api-Service: ApiConversationService")
    suspend fun getConversationDetail(
        @Path("id") id: Int
    ): Response<ConversationDto>

    @GET(ApiConstant.API_KEY_CONVERSATION_MESSAGES)
    @Headers("X-Api-Service: ApiConversationService")
    suspend fun getConversationMessages(
        @Path("id") id: Int,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("search") search: String? = null
    ): Response<ConversationMessagesResponseDto>

    @POST(ApiConstant.API_KEY_CONVERSATION_MESSAGE_CREATE)
    @Headers("X-Api-Service: ApiConversationService")
    suspend fun createMessage(
        @Path("id") id: Int,
        @Body request: CreateMessageRequestDto
    ): Response<Unit>

    @PUT(ApiConstant.API_KEY_CONVERSATION_MESSAGE_CHANGE_READ_STATUS)
    @Headers("X-Api-Service: ApiConversationService")
    suspend fun changeMessageReadStatus(
        @Path("id_conversation") conversationId: Int,
        @Path("id_message") messageId: Int
    ): Response<Void>

    @PUT(ApiConstant.API_KEY_CONVERSATION_MESSAGES_CHANGE_ALL_TO_READ)
    @Headers("X-Api-Service: ApiConversationService")
    suspend fun changeAllMessagesToRead(): Response<Void>

    @GET(ApiConstant.API_KEY_CONVERSATION_MESSAGE_WORK_LIST)
    @Headers("X-Api-Service: ApiConversationService")
    suspend fun getWorkConversationMessages(
        @Path("id") workId: Int,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ConversationMessagesResponseDto>

    @GET(ApiConstant.API_KEY_CONVERSATION_WORK_DETAIL)
    @Headers("X-Api-Service: ApiConversationService")
    suspend fun getConversationWork(
        @Path("id") id: Int
    ): Response<ConversationWorkResponseDto>
}
