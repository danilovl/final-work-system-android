@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class LastMessageResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("owner")
    val owner: ConversationUserResponse = ConversationUserResponse(),
    @SerialName("createdAt")
    val createdAt: String = ""
)

fun LastMessageResponse.toDomain(): com.finalworksystem.domain.conversation.model.LastMessage {
    return com.finalworksystem.domain.conversation.model.LastMessage(
        id = id,
        owner = owner.toDomain(),
        createdAt = createdAt
    )
}
