package com.finalworksystem.data.work.model.response

import com.google.gson.annotations.SerializedName
import com.finalworksystem.domain.work.model.WorkType as DomainWorkType

data class TypeResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?
)

fun TypeResponse.toDataModel(): DomainWorkType {
    return DomainWorkType(
        id = id,
        name = name,
        description = description
    )
}