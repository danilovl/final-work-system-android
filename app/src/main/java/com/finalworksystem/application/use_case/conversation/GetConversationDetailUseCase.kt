package com.finalworksystem.application.use_case.conversation

import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.domain.conversation.repository.GetConversationRepository
import kotlinx.coroutines.flow.Flow

class GetConversationDetailUseCase(private val conversationRepository: GetConversationRepository) {
    operator fun invoke(id: Int): Flow<Result<Conversation>> {
        return conversationRepository.getConversationDetail(id)
    }
}
