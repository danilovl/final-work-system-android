@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.work.model.response

import com.finalworksystem.domain.user.model.User as DomainUser
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

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

    @SerialName("fullName")
    val fullName: String? = null,

    @SerialName("degreeBefore")
    val degreeBefore: String? = null,

    @SerialName("degreeAfter")
    val degreeAfter: String? = null,

    @SerialName("email")
    val email: String = "",

    @SerialName("token")
    val token: String? = null,

    @SerialName("roles")
    val roles: JsonElement? = null
)

fun UserResponse.toDataModel(): DomainUser {
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
        roles = when (roles) {
            is JsonObject -> roles.values.map { it.roleName() }
            is JsonArray -> roles.map { it.roleName() }
            else -> emptyList()
        }
    )
}

/** Roles come as strings; keep the raw text for anything else. */
private fun JsonElement.roleName(): String =
    if (this is JsonPrimitive) content else toString()
