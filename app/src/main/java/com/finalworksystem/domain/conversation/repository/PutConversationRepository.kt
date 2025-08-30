package com.finalworksystem.domain.conversation.repository

import kotlinx.coroutines.flow.Flow

interface PutConversationRepository {
    fun changeMessageReadStatus(
        conversationId: Int,
        messageId: Int
    ): Flow<Result<Boolean>>

    fun changeAllMessagesToRead(): Flow<Result<Boolean>>
}