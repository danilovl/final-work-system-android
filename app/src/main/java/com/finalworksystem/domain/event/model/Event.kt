package com.finalworksystem.domain.event.model

import com.finalworksystem.domain.user.model.User

data class Event(
    val id: Int,
    val type: EventType,
    val name: String?,
    val start: String,
    val end: String,
    val owner: User,
    val address: EventAddress?,
    val comment: List<Comment>?,
    val participant: Participant?
)
