package com.finalworksystem.data.work.model.response

import com.google.gson.annotations.SerializedName
import com.finalworksystem.domain.work.model.WorkStatus as DomainWorkStatus

data class StatusResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?
)

fun StatusResponse.toDataModel(): DomainWorkStatus {
    return DomainWorkStatus(
        id = id,
        name = name,
        description = description
    )
}