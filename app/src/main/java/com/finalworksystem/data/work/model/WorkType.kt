package com.finalworksystem.data.work.model

import com.finalworksystem.domain.work.model.WorkType as DomainWorkType

data class WorkType(
    val id: Int,
    val name: String,
    val description: String?
)

fun WorkType.toDomainModel(): DomainWorkType {
    return DomainWorkType(
        id = id,
        name = name,
        description = description
    )
}
