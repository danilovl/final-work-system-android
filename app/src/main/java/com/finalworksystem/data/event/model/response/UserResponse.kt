@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.User
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("username")
    val username: String = "",

    @SerialName("firstname")
    val firstname: String = "",

    @SerialName("lastname")
    val lastname: String = "",

    @SerialName("degreeBefore")
    val degreeBefore: String? = null,

    @SerialName("degreeAfter")
    val degreeAfter: String? = null,

    @SerialName("email")
    val email: String = "",

    @SerialName("roles")
    val roles: List<String> = emptyList()
)

fun UserResponse.toDataModel(): User {
    return User(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        roles = roles
    )
}

