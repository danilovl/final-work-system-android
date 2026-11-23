@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.work.model.response

import com.finalworksystem.domain.work.model.WorkStatus as DomainWorkStatus
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class StatusResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String = "",

    @SerialName("description")
    val description: String? = null,

    @SerialName("color")
    val color: String? = null
)

fun StatusResponse.toDataModel(): DomainWorkStatus {
    return DomainWorkStatus(
        id = id,
        name = name,
        description = description,
        color = color
    )
}