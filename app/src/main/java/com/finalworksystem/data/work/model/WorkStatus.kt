package com.finalworksystem.data.work.model

import com.finalworksystem.domain.work.model.WorkStatus as DomainWorkStatus

data class WorkStatus(
    val id: Int,
    val name: String,
    val description: String?
)

fun WorkStatus.toDomainModel(): DomainWorkStatus {
    return DomainWorkStatus(
        id = id,
        name = name,
        description = description
    )
}
