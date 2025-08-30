package com.finalworksystem.data.work.model.response

import com.finalworksystem.data.work.model.Work
import com.google.gson.annotations.SerializedName

data class WorkListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("result")
    val result: List<WorkResponse>
)

fun WorkListResponse.toDataModel(): List<Work> {
    return result.map { it.toDataModel() }
}