@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event_calendar.model.request

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventCalendarCreateRequest(
    @SerialName("typeId")
    val typeId: Int,

    @SerialName("name")
    val name: String?,

    @SerialName("addressId")
    val addressId: Int,

    @SerialName("userId")
    val userId: Int?,

    @SerialName("workId")
    val workId: Int?,

    @SerialName("start")
    val start: String,

    @SerialName("end")
    val end: String
)
