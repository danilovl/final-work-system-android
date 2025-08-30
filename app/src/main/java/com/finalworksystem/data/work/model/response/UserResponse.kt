package com.finalworksystem.data.work.model.response

import com.google.gson.annotations.SerializedName
import com.finalworksystem.domain.user.model.User as DomainUser

data class UserResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("lastname")
    val lastname: String,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("degreeBefore")
    val degreeBefore: String?,

    @SerializedName("degreeAfter")
    val degreeAfter: String?,

    @SerializedName("email")
    val email: String,

    @SerializedName("token")
    val token: String?,

    @SerializedName("roles")
    val roles: Any
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
            is Map<*, *> -> roles.values.map { it.toString() }
            is List<*> -> roles.map { it.toString() }
            else -> emptyList()
        }
    )
}