package com.finalworksystem.data.event.model

import com.finalworksystem.data.conversation.model.response.WorkStatusDto
import com.finalworksystem.domain.work.model.WorkStatus as DomainWorkStatus

data class WorkStatus(
    val id: Int,
    val name: String,
    val description: String?
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
