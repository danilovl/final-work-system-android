@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.user.model

import com.finalworksystem.domain.user.model.User as DomainUser
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class User(
    val id: Int = 0,
    val username: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val fullName: String? = null,
    val degreeBefore: String? = null,
    val degreeAfter: String? = null,
    val email: String = "",
    val token: String = "",
    val roles: List<String> = emptyList()
)

fun User.toDomainModel(): DomainUser {
    return DomainUser(
        id = id,
        username = username,
        firstname = firstname,
        lastname = lastname,
        fullName = fullName,
        degreeBefore = degreeBefore,
        degreeAfter = degreeAfter,
        email = email,
        token = token,
        roles = roles
    )
}
