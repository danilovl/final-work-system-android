package com.finalworksystem.data.event_calendar.model.response

import com.google.gson.annotations.SerializedName

data class EventCalendarCreateResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("start")
    val start: String,

    @SerializedName("end")
    val end: String,

    @SerializedName("detail_url")
    val detailUrl: String,

    @SerializedName("delete_url")
    val deleteUrl: String
)