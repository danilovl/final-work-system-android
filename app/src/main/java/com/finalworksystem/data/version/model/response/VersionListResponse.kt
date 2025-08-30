package com.finalworksystem.data.version.model.response

import com.finalworksystem.data.version.model.Version
import com.google.gson.annotations.SerializedName

data class VersionListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("result")
    val result: List<VersionResponse>
)

fun VersionListResponse.toDataModel(): List<Version> {
    return result.map { it.toDataModel() }
}