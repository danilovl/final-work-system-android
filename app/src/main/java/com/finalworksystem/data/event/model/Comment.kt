@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.Comment as DomainComment
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Comment(
    val id: Int = 0,
    val content: String = "",
    val owner: User
)

fun Comment.toDomainModel(): DomainComment {
    return DomainComment(
        id = id,
        content = content,
        owner = owner.toDomainModel()
    )
}
