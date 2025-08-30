package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.EventType as DomainEventType

data class EventType(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String?,
    val registrable: Boolean?
)


fun EventType.toDomainModel(): DomainEventType {
    return DomainEventType(
        id = id,
        name = name,
        description = description,
        color = color,
        registrable = registrable
    )
}
