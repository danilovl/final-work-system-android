@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationUserDto(
    val id: Int = 0,
    val username: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val degreeBefore: String? = null,
    val degreeAfter: String? = null
)

fun ConversationUserDto.toEntity(): com.finalworksystem.domain.user.model.User {
    return com.finalworksystem.domain.user.model.User(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        fullName = null,
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        token = null,
        roles = emptyList()
    )
}
