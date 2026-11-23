@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event_calendar.model.response

import com.finalworksystem.data.event.model.response.EventAddressResponse
import com.finalworksystem.data.event.model.response.ParticipantResponse
import com.finalworksystem.data.event.model.response.toDataModel
import com.finalworksystem.data.event.model.toDomainModel
import com.finalworksystem.domain.event_calendar.model.EventCalendarManageCreateData
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventCalendarManageCreateDataDto(
    @SerialName("types")
    val types: List<EventTypeDto> = emptyList(),

    @SerialName("addresses")
    val addresses: List<EventAddressResponse> = emptyList(),

    @SerialName("participants")
    val participants: List<ParticipantResponse> = emptyList()
)

fun EventCalendarManageCreateDataDto.toDomainModel(): EventCalendarManageCreateData {
    return EventCalendarManageCreateData(
        types = types.map { it.toDomainModel() },
        addresses = addresses.map { it.toDataModel().toDomainModel() },
        participants = participants.map { it.toDataModel().toDomainModel() }
    )
}
