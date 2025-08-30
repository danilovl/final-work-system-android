package com.finalworksystem.data.event_calendar.model.response

import com.finalworksystem.data.event.model.response.ParticipantResponse
import com.finalworksystem.data.event.model.response.toDataModel
import com.finalworksystem.data.event.model.toDomainModel
import com.finalworksystem.domain.event_calendar.model.CalendarEvent
import com.google.gson.annotations.SerializedName

data class EventCalendarDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("color")
    val color: String?,

    @SerializedName("start")
    val start: String,

    @SerializedName("end")
    val end: String,

    @SerializedName("is_free")
    val isFree: Boolean? = null,

    @SerializedName("participant")
    val participant: ParticipantResponse? = null,

    @SerializedName("hasParticipant")
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
