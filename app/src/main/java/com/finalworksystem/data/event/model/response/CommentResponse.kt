package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.Comment
import com.google.gson.annotations.SerializedName

data class CommentResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("content")
    val content: String,

    @SerializedName("owner")
    val owner: UserResponse
)

fun CommentResponse.toDataModel(): Comment {
    return Comment(
        id = id,
        content = content,
        owner = owner.toDataModel()
    )
}
