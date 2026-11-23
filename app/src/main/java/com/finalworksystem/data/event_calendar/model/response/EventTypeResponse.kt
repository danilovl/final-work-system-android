@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event_calendar.model.response

import com.finalworksystem.domain.event.model.EventType
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventTypeDto(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String = "",

    @SerialName("description")
    val description: String? = null,

    @SerialName("color")
    val color: String = "",

    @SerialName("registrable")
    val registrable: Boolean = false
)

fun EventTypeDto.toDomainModel(): EventType {
    return EventType(
        id = id,
        name = name,
        description = description,
        color = color,
        registrable = registrable
    )
}
