@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.work.model.response

import com.finalworksystem.domain.work.model.WorkCategory
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WorkCategoryDto(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String = "",

    @SerialName("description")
    val description: String? = null,

    @SerialName("sorting")
    val sorting: Int = 0
)

fun WorkCategoryDto.toDomainModel(): WorkCategory {
    return WorkCategory(
        id = id,
        name = name,
        description = description,
        sorting = sorting
    )
}
