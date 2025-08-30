package com.finalworksystem.domain.event.model

data class EventType(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String?,
    val registrable: Boolean?
)
