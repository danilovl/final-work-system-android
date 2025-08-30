package com.finalworksystem.application.use_case.conversation

import com.finalworksystem.domain.conversation.model.response.ConversationMessagesResponse
import com.finalworksystem.domain.conversation.repository.GetConversationRepository
import kotlinx.coroutines.flow.Flow

class GetWorkConversationMessagesUseCase(private val conversationRepository: GetConversationRepository) {
    operator fun invoke(
        workId: Int,
        page: Int? = null,
        limit: Int? = null
    ): Flow<Result<ConversationMessagesResponse>> {
        return conversationRepository.getWorkConversationMessages(workId, page, limit)
    }
}
