@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.Event as DomainEvent
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Event(
    val id: Int = 0,
    val type: EventType,
    val name: String? = null,
    val start: String = "",
    val end: String = "",
    val owner: User,
    val address: EventAddress? = null,
    val comment: List<Comment>? = null,
    val participant: Participant? = null
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
