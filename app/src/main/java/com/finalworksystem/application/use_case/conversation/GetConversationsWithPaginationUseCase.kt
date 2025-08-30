package com.finalworksystem.application.use_case.conversation

import com.finalworksystem.domain.conversation.model.response.ConversationsResponse
import com.finalworksystem.domain.conversation.repository.GetConversationRepository
import kotlinx.coroutines.flow.Flow

class GetConversationsWithPaginationUseCase(private val conversationRepository: GetConversationRepository) {
    operator fun invoke(page: Int, limit: Int, search: String? = null): Flow<Result<ConversationsResponse>> {
        return conversationRepository.getConversations(page, limit, search)
    }
}
