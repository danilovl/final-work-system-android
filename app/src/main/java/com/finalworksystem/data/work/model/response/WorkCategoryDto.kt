package com.finalworksystem.data.work.model.response

import com.finalworksystem.domain.work.model.WorkCategory
import com.google.gson.annotations.SerializedName

data class WorkCategoryDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("sorting")
    val sorting: Int
)

fun WorkCategoryDto.toDomainModel(): WorkCategory {
    return WorkCategory(
        id = id,
        name = name,
        description = description,
        sorting = sorting
    )
}
