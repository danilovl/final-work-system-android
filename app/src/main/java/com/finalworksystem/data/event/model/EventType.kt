@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model

import com.finalworksystem.domain.event.model.EventType as DomainEventType
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventType(
    val id: Int = 0,
    val name: String = "",
    val description: String? = null,
    val color: String? = null,
    val registrable: Boolean? = null
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
