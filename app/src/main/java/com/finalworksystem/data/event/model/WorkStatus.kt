@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model

import com.finalworksystem.data.conversation.model.response.WorkStatusDto
import com.finalworksystem.domain.work.model.WorkStatus as DomainWorkStatus
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WorkStatus(
    val id: Int = 0,
    val name: String = "",
    val description: String? = null
)

fun WorkStatusDto.toDataModel(): WorkStatus {
    return WorkStatus(
        id = id,
        name = name,
        description = description
    )
}

fun WorkStatus.toDomainModel(): DomainWorkStatus {
    return DomainWorkStatus(
        id = id,
        name = name,
        description = description
    )
}
