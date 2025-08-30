package com.finalworksystem.domain.conversation.repository

import kotlinx.coroutines.flow.Flow

interface PostConversationRepository {
    fun createMessage(
        conversationId: Int,
        message: String
    ): Flow<Result<Unit>>
}