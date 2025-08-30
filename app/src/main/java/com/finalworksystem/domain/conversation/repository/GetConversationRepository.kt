package com.finalworksystem.domain.conversation.repository

import com.finalworksystem.domain.conversation.model.Conversation
import com.finalworksystem.domain.conversation.model.response.ConversationMessagesResponse
import com.finalworksystem.domain.conversation.model.response.ConversationWorkResponse
import com.finalworksystem.domain.conversation.model.response.ConversationsResponse
import kotlinx.coroutines.flow.Flow

interface GetConversationRepository {
    fun getConversations(
        page: Int,
        limit: Int,
        search: String? = null
    ): Flow<Result<ConversationsResponse>>

    fun getConversationDetail(id: Int): Flow<Result<Conversation>>

    fun getConversationMessages(
        id: Int,
        page: Int? = null,
        limit: Int? = null,
        search: String? = null
    ): Flow<Result<ConversationMessagesResponse>>

    fun getWorkConversationMessages(
        workId: Int,
        page: Int? = null,
        limit: Int? = null
    ): Flow<Result<ConversationMessagesResponse>>

    fun getConversationWork(id: Int): Flow<Result<ConversationWorkResponse?>>
}