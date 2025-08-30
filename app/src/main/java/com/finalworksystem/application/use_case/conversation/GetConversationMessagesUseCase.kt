package com.finalworksystem.application.use_case.conversation

import com.finalworksystem.domain.conversation.model.response.ConversationMessagesResponse
import com.finalworksystem.domain.conversation.repository.GetConversationRepository
import kotlinx.coroutines.flow.Flow

class GetConversationMessagesUseCase(private val conversationRepository: GetConversationRepository) {
    operator fun invoke(
        id: Int,
        page: Int? = null,
        limit: Int? = null,
        search: String? = null
    ): Flow<Result<ConversationMessagesResponse>> {
        return conversationRepository.getConversationMessages(id, page, limit, search)
    }
}
