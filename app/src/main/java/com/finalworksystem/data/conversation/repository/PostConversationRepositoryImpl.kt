package com.finalworksystem.data.conversation.repository

import com.finalworksystem.data.conversation.model.request.CreateMessageRequestDto
import com.finalworksystem.data.util.handleSimpleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.conversation.repository.PostConversationRepository
import com.finalworksystem.infrastructure.api.ApiConversationService
import kotlinx.coroutines.flow.Flow

class PostConversationRepositoryImpl(
    private val apiConversationService: ApiConversationService
) : PostConversationRepository {

    override fun createMessage(
        conversationId: Int,
        message: String
    ): Flow<Result<Unit>> = safeFlowResult {
        val request = CreateMessageRequestDto(message = message)
        val response = apiConversationService.createMessage(conversationId, request)

        response.handleSimpleResponse()
    }
}
