package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.EventType
import com.google.gson.annotations.SerializedName

data class EventTypeResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("color")
    val color: String?,

    @SerializedName("registrable")
    val registrable: Boolean?
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
