package com.finalworksystem.data.conversation.model.response

import com.google.gson.annotations.SerializedName

data class ConversationUserResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("firstname")
    val firstname: String,

    @SerializedName("lastname")
    val lastname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("degreeBefore")
    val degreeBefore: String?,

    @SerializedName("degreeAfter")
    val degreeAfter: String?
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
