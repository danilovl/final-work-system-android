package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.User
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("lastname")
    val lastname: String,

    @SerializedName("degreeBefore")
    val degreeBefore: String?,

    @SerializedName("degreeAfter")
    val degreeAfter: String?,

    @SerializedName("email")
    val email: String,

    @SerializedName("roles")
    val roles: List<String>
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

