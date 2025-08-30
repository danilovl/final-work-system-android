package com.finalworksystem.data.conversation.model.response

import com.google.gson.annotations.SerializedName

data class LastMessageResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("owner")
    val owner: ConversationUserResponse,

    @SerializedName("createdAt")
    val createdAt: String
)

fun LastMessageResponse.toDomain(): com.finalworksystem.domain.conversation.model.LastMessage {
    return com.finalworksystem.domain.conversation.model.LastMessage(
        id = id,
        owner = owner.toDomain(),
        createdAt = createdAt
    )
}
