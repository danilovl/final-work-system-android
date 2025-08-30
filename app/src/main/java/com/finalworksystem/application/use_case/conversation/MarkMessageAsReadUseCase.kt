package com.finalworksystem.application.use_case.conversation

import com.finalworksystem.domain.conversation.repository.PutConversationRepository
import kotlinx.coroutines.flow.Flow

class MarkMessageAsReadUseCase(private val conversationRepository: PutConversationRepository) {
    operator fun invoke(conversationId: Int, messageId: Int): Flow<Result<Boolean>> {
        return conversationRepository.changeMessageReadStatus(conversationId, messageId)
    }
}
