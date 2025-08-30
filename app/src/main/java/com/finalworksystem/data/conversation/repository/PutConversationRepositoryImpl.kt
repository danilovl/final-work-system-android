package com.finalworksystem.data.conversation.repository

import com.finalworksystem.data.util.handleSimpleResponse
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.conversation.repository.PutConversationRepository
import com.finalworksystem.infrastructure.api.ApiConversationService
import kotlinx.coroutines.flow.Flow

class PutConversationRepositoryImpl(
    private val apiConversationService: ApiConversationService
) : PutConversationRepository {

    override fun changeMessageReadStatus(
        conversationId: Int,
        messageId: Int
    ): Flow<Result<Boolean>> = safeFlowResult {
        val response = apiConversationService.changeMessageReadStatus(conversationId, messageId)

        response.handleSimpleResponse().map { true }
    }

    override fun changeAllMessagesToRead(): Flow<Result<Boolean>> = safeFlowResult {
        val response = apiConversationService.changeAllMessagesToRead()

        response.handleSimpleResponse().map { true }
    }
}
