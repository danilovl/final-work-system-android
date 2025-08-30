package com.finalworksystem.application.use_case.conversation

import com.finalworksystem.domain.conversation.repository.PostConversationRepository
import kotlinx.coroutines.flow.Flow

class CreateMessageUseCase(private val conversationRepository: PostConversationRepository) {
    operator fun invoke(
        conversationId: Int,
        message: String
    ): Flow<Result<Unit>> {
        return conversationRepository.createMessage(conversationId, message)
    }
}
