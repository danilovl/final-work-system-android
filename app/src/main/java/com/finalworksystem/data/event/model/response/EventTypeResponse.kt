@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.EventType
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventTypeResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String = "",

    @SerialName("description")
    val description: String? = null,

    @SerialName("color")
    val color: String? = null,

    @SerialName("registrable")
    val registrable: Boolean? = null
)

fun EventTypeResponse.toDataModel(): EventType {
    return EventType(
        id = id,
        name = name,
        description = description,
        color = color,
        registrable = registrable
    )
}
