@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model

import com.finalworksystem.domain.conversation.model.response.ConversationsResponse
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class PaginatedConversationsDto(
    val conversations: List<ConversationDto> = emptyList(),
    val totalCount: Int = 0,
    val currentItemCount: Int = 0,
    val numItemsPerPage: Int = 0
)

fun PaginatedConversationsDto.toEntity(): ConversationsResponse {
    return ConversationsResponse(
        conversations = conversations.toEntityList(),
        totalCount = totalCount,
        currentItemCount = currentItemCount,
        numItemsPerPage = numItemsPerPage
    )
}
