package com.finalworksystem.data.conversation.repository

import com.finalworksystem.data.conversation.model.response.toDomain
import com.finalworksystem.data.conversation.model.response.toDomainList
import com.finalworksystem.data.conversation.model.toEntity
import com.finalworksystem.data.util.handleResponse
import com.finalworksystem.data.util.handleResponseWithNullable
import com.finalworksystem.domain.common.util.safeFlowResult
import com.finalworksystem.domain.conversation.model.ConversationMessage
import com.finalworksystem.domain.conversation.model.ConversationParticipant
import com.finalworksystem.domain.conversation.model.ConversationType
import com.finalworksystem.domain.conversation.model.ConversationWork
import com.finalworksystem.domain.conversation.model.LastMessage
import com.finalworksystem.domain.conversation.model.response.ConversationMessagesResponse
import com.finalworksystem.domain.conversation.model.response.ConversationWorkResponse
import com.finalworksystem.domain.conversation.model.response.ConversationsResponse
import com.finalworksystem.domain.conversation.repository.GetConversationRepository
import com.finalworksystem.infrastructure.api.ApiConversationService
import kotlinx.coroutines.flow.Flow
import com.finalworksystem.domain.conversation.model.Conversation as DomainConversation

class GetConversationRepositoryImpl(
    private val apiConversationService: ApiConversationService
) : GetConversationRepository {

    override fun getConversations(
        page: Int,
        limit: Int,
        search: String?
    ): Flow<Result<ConversationsResponse>> = safeFlowResult {
        val response = apiConversationService.getConversations(page = page, limit = limit, search = search)

        response.handleResponse { conversationListResponse ->
            val apiConversations = conversationListResponse.result
            val domainConversations = apiConversations.toDomainList()

            ConversationsResponse(
                conversations = domainConversations,
                totalCount = conversationListResponse.totalCount,
                currentItemCount = conversationListResponse.currentItemCount,
                numItemsPerPage = limit
            )
        }
    }

    override fun getConversationDetail(id: Int): Flow<Result<DomainConversation>> = safeFlowResult {
        val response = apiConversationService.getConversationDetail(id)

        response.handleResponse { conversationDto ->
            conversationDto.toEntity()
        }
    }

    override fun getConversationMessages(
        id: Int,
        page: Int?,
        limit: Int?,
        search: String?
    ): Flow<Result<ConversationMessagesResponse>> = safeFlowResult {
        val response = apiConversationService.getConversationMessages(id, page, limit, search)

        response.handleResponse { messagesResponseDto ->
            ConversationMessagesResponse(
                numItemsPerPage = messagesResponseDto.numItemsPerPage,
                totalCount = messagesResponseDto.totalCount,
                currentItemCount = messagesResponseDto.currentItemCount,
                result = messagesResponseDto.result.map { messageDto ->
                    ConversationMessage(
                        id = messageDto.id,
                        owner = messageDto.owner.toEntity(),
                        content = messageDto.content,
                        isRead = messageDto.isRead,
                        createdAt = messageDto.createdAt
                    )
                }
            )
        }
    }

    override fun getWorkConversationMessages(
        workId: Int,
        page: Int?,
        limit: Int?
    ): Flow<Result<ConversationMessagesResponse>> = safeFlowResult {
        val response = apiConversationService.getWorkConversationMessages(workId, page, limit)

        response.handleResponse { messagesResponseDto ->
            ConversationMessagesResponse(
                numItemsPerPage = messagesResponseDto.numItemsPerPage,
                totalCount = messagesResponseDto.totalCount,
                currentItemCount = messagesResponseDto.currentItemCount,
                result = messagesResponseDto.result.map { messageDto ->
                    ConversationMessage(
                        id = messageDto.id,
                        owner = messageDto.owner.toEntity(),
                        content = messageDto.content,
                        isRead = messageDto.isRead,
                        createdAt = messageDto.createdAt
                    )
                }
            )
        }
    }

    override fun getConversationWork(id: Int): Flow<Result<ConversationWorkResponse?>> = safeFlowResult {
        val response = apiConversationService.getConversationWork(id)

        response.handleResponseWithNullable().map { conversationWorkResponseDto ->
            conversationWorkResponseDto?.let { dto ->
                ConversationWorkResponse(
                    id = dto.id,
                    name = dto.name,
                    isRead = dto.isRead,
                    recipient = dto.recipient?.toEntity(),
                    work = dto.work?.let { workDto ->
                        ConversationWork(
                            id = workDto.id,
                            title = workDto.title,
                            shortcut = workDto.shortcut,
                            deadline = workDto.deadline,
                            deadlineProgram = workDto.deadlineProgram,
                            status = workDto.status.toDomain(),
                            type = workDto.type.toDomain(),
                            author = workDto.author?.toEntity(),
                            supervisor = workDto.supervisor?.toEntity(),
                            opponent = workDto.opponent?.toEntity(),
                            consultant = workDto.consultant?.toEntity()
                        )
                    },
                    participants = dto.participants.map { participantDto ->
                        ConversationParticipant(
                            id = participantDto.id,
                            user = participantDto.user.toEntity()
                        )
                    },
                    lastMessage = dto.lastMessage?.let { messageDto ->
                        LastMessage(
                            id = messageDto.id,
                            owner = messageDto.owner.toEntity(),
                            createdAt = messageDto.createdAt
                        )
                    },
                    type = ConversationType(
                        id = dto.type.id,
                        name = dto.type.name,
                        constant = dto.type.constant
                    )
                )
            }
        }
    }
}
