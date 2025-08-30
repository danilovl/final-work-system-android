package com.finalworksystem.data.version.model

import com.finalworksystem.data.work.model.response.TypeResponse
import com.finalworksystem.domain.version.model.VersionType as DomainVersionType

data class VersionType(
    val id: Int,
    val name: String,
    val description: String?
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