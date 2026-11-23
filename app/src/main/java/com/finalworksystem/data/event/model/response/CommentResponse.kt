@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.Comment
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class CommentResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("content")
    val content: String = "",

    @SerialName("owner")
    val owner: UserResponse = UserResponse()
)

fun CommentResponse.toDataModel(): Comment {
    return Comment(
        id = id,
        content = content,
        owner = owner.toDataModel()
    )
}
