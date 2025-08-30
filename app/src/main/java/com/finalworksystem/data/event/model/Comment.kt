package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.Comment as DomainComment

data class Comment(
    val id: Int,
    val content: String,
    val owner: User
)

fun Comment.toDomainModel(): DomainComment {
    return DomainComment(
        id = id,
        content = content,
        owner = owner.toDomainModel()
    )
}
