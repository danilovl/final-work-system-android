package com.finalworksystem.data.event_calendar.model.response

import com.finalworksystem.data.event.model.response.EventAddressResponse
import com.finalworksystem.data.event.model.response.ParticipantResponse
import com.finalworksystem.data.event.model.response.toDataModel
import com.finalworksystem.data.event.model.toDomainModel
import com.finalworksystem.domain.event_calendar.model.EventCalendarManageCreateData
import com.google.gson.annotations.SerializedName

data class EventCalendarManageCreateDataDto(
    @SerializedName("types")
    val types: List<EventTypeDto>,

    @SerializedName("addresses")
    val addresses: List<EventAddressResponse>,

    @SerializedName("participants")
    val participants: List<ParticipantResponse>
)

fun EventCalendarManageCreateDataDto.toDomainModel(): EventCalendarManageCreateData {
    return EventCalendarManageCreateData(
        types = types.map { it.toDomainModel() },
        addresses = addresses.map { it.toDataModel().toDomainModel() },
        participants = participants.map { it.toDataModel().toDomainModel() }
    )
}
