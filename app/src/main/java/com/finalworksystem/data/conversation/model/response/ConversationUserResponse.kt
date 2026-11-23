@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model.response

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationUserResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("username")
    val username: String = "",

    @SerialName("firstname")
    val firstname: String = "",

    @SerialName("lastname")
    val lastname: String = "",

    @SerialName("email")
    val email: String = "",

    @SerialName("degreeBefore")
    val degreeBefore: String? = null,

    @SerialName("degreeAfter")
    val degreeAfter: String? = null
)

fun ConversationUserResponse.toDomain(): com.finalworksystem.domain.user.model.User {
    return com.finalworksystem.domain.user.model.User(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        fullName = "$firstname $lastname",
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        token = null,
        roles = emptyList()
    )
}
