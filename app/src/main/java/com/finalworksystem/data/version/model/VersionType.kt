@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.version.model

import com.finalworksystem.data.work.model.response.TypeResponse
import com.finalworksystem.domain.version.model.VersionType as DomainVersionType
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class VersionType(
    val id: Int = 0,
    val name: String = "",
    val description: String? = null
)

fun TypeResponse.toVersionType(): VersionType {
    return VersionType(
        id = id,
        name = name,
        description = description
    )
}

fun VersionType.toDomainModel(): DomainVersionType {
    return DomainVersionType(
        id = id,
        name = name,
        description = description
    )
}