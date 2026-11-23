@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event_calendar.model.response

import com.finalworksystem.data.event.model.response.ParticipantResponse
import com.finalworksystem.data.event.model.response.toDataModel
import com.finalworksystem.data.event.model.toDomainModel
import com.finalworksystem.domain.event_calendar.model.CalendarEvent
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventCalendarDto(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("title")
    val title: String = "",

    @SerialName("color")
    val color: String? = null,

    @SerialName("start")
    val start: String = "",

    @SerialName("end")
    val end: String = "",

    @SerialName("is_free")
    val isFree: Boolean? = null,

    @SerialName("participant")
    val participant: ParticipantResponse? = null,

    @SerialName("hasParticipant")
    val hasParticipant: Boolean = false
)

fun EventCalendarDto.toDomainModel(): CalendarEvent {
    return CalendarEvent(
        id = this.id,
        title = this.title,
        color = this.color,
        start = this.start,
        end = this.end,
        isFree = this.isFree,
        participant = this.participant?.toDataModel()?.toDomainModel(),
        hasParticipant = this.hasParticipant
    )
}
