@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.work.model

import com.finalworksystem.domain.work.model.WorkType as DomainWorkType
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WorkType(
    val id: Int = 0,
    val name: String = "",
    val description: String? = null
)

fun WorkType.toDomainModel(): DomainWorkType {
    return DomainWorkType(
        id = id,
        name = name,
        description = description
    )
}
