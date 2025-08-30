package com.finalworksystem.data.event_calendar.model.request

import com.google.gson.annotations.SerializedName

data class EventCalendarCreateRequest(
    @SerializedName("typeId")
    val typeId: Int,

    @SerializedName("name")
    val name: String?,

    @SerializedName("addressId")
    val addressId: Int,

    @SerializedName("userId")
    val userId: Int?,

    @SerializedName("workId")
    val workId: Int?,

    @SerializedName("start")
    val start: String,

    @SerializedName("end")
    val end: String
)
