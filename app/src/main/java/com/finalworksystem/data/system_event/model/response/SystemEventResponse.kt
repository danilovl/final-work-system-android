package com.finalworksystem.data.system_event.model.response

import com.google.gson.annotations.SerializedName

data class SystemEvent(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("owner")
    val owner: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("viewed")
    val viewed: Boolean = false
)

data class SystemEventListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("result")
    val result: List<SystemEvent>
)

