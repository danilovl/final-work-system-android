@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.event.model.Event
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("type")
    val type: EventTypeResponse = EventTypeResponse(),
    @SerialName("name")
    val name: String? = null,

    @SerialName("start")
    val start: String = "",

    @SerialName("end")
    val end: String = "",

    @SerialName("owner")
    val owner: UserResponse = UserResponse(),
    @SerialName("address")
    val address: EventAddressResponse? = null,

    @SerialName("comment")
    val comment: List<CommentResponse>? = null,

    @SerialName("participant")
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
