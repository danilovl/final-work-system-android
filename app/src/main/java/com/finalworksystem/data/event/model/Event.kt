package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.Event as DomainEvent

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

fun Event.toDomainModel(): DomainEvent {
    return DomainEvent(
        id = id,
        type = type.toDomainModel(),
        name = name,
        start = start,
        end = end,
        owner = owner.toDomainModel(),
        address = address?.toDomainModel(),
        comment = comment?.map { it.toDomainModel() },
        participant = participant?.toDomainModel()
    )
}
