package com.finalworksystem.data.event.model.response

import com.google.gson.annotations.SerializedName

data class EventListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("result")
    val result: List<EventResponse>
)