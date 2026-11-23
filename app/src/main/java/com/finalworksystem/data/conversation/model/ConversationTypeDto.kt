@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationTypeDto(
    val id: Int = 0,
    val name: String = "",
    val constant: String = ""
)

fun ConversationTypeDto.toEntity(): com.finalworksystem.domain.conversation.model.ConversationType {
    return com.finalworksystem.domain.conversation.model.ConversationType(
        id = id,
        name = name,
        constant = constant
    )
}
