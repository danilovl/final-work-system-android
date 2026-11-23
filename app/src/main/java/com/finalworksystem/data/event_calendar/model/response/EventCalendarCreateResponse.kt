@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event_calendar.model.response

import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class EventCalendarCreateResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("title")
    val title: String = "",

    @SerialName("color")
    val color: String = "",

    @SerialName("start")
    val start: String = "",

    @SerialName("end")
    val end: String = "",

    @SerialName("detail_url")
    val detailUrl: String = "",

    @SerialName("delete_url")
    val deleteUrl: String = ""
)