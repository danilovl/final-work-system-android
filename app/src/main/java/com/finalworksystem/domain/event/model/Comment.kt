package com.finalworksystem.domain.event.model

import com.finalworksystem.domain.user.model.User

data class Comment(
    val id: Int,
    val content: String,
    val owner: User
)
