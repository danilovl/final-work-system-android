package com.finalworksystem.application.use_case.conversation

import com.finalworksystem.domain.conversation.model.response.ConversationWorkResponse
import com.finalworksystem.domain.conversation.repository.GetConversationRepository
import kotlinx.coroutines.flow.Flow

class GetConversationWorkUseCase(private val conversationRepository: GetConversationRepository) {
    operator fun invoke(id: Int): Flow<Result<ConversationWorkResponse?>> {
        return conversationRepository.getConversationWork(id)
    }
}
