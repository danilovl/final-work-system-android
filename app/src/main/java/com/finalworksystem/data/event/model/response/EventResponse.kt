package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.Event
import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("type")
    val type: EventTypeResponse,

    @SerializedName("name")
    val name: String?,

    @SerializedName("start")
    val start: String,

    @SerializedName("end")
    val end: String,

    @SerializedName("owner")
    val owner: UserResponse,

    @SerializedName("address")
    val address: EventAddressResponse?,

    @SerializedName("comment")
    val comment: List<CommentResponse>? = null,

    @SerializedName("participant")
    val participant: ParticipantResponse? = null
)

fun EventResponse.toDataModel(): Event {
    return Event(
        id = id,
        type = type.toDataModel(),
        name = name,
        start = start,
        end = end,
        owner = owner.toDataModel(),
        address = address?.toDataModel(),
        comment = comment?.map { it.toDataModel() },
        participant = participant?.toDataModel()
    )
}
