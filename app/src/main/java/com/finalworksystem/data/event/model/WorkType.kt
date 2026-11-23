@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model

import com.finalworksystem.data.conversation.model.response.WorkTypeDto
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

fun WorkTypeDto.toDataModel(): WorkType {
    return WorkType(
        id = id,
        name = name,
        description = description
    )
}

fun WorkType.toDomainModel(): DomainWorkType {
    return DomainWorkType(
        id = id,
        name = name,
        description = description
    )
}
