package com.finalworksystem.application.use_case.conversation

import com.finalworksystem.domain.conversation.repository.PutConversationRepository
import kotlinx.coroutines.flow.Flow

class MarkAllMessagesAsReadUseCase(private val conversationRepository: PutConversationRepository) {
    operator fun invoke(): Flow<Result<Boolean>> {
        return conversationRepository.changeAllMessagesToRead()
    }
}
