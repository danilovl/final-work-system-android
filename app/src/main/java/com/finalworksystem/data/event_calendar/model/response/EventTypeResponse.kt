package com.finalworksystem.data.event_calendar.model.response

import com.finalworksystem.domain.event.model.EventType
import com.google.gson.annotations.SerializedName

data class EventTypeDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("color")
    val color: String,

    @SerializedName("registrable")
    val registrable: Boolean
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
