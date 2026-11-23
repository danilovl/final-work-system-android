@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.data.conversation.model.ConversationMessageDto
import com.finalworksystem.data.conversation.model.toEntityList
import com.finalworksystem.domain.conversation.model.response.ConversationMessagesResponse
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationMessagesResponseDto(
    val numItemsPerPage: Int = 0,
    val totalCount: Int = 0,
    val currentItemCount: Int = 0,
    val result: List<ConversationMessageDto> = emptyList()
)

fun ConversationMessagesResponseDto.toEntity(): ConversationMessagesResponse {
    return ConversationMessagesResponse(
        numItemsPerPage = numItemsPerPage,
        totalCount = totalCount,
        currentItemCount = currentItemCount,
        result = result.toEntityList()
    )
}
