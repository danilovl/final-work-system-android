package com.finalworksystem.data.event.model

import com.finalworksystem.data.conversation.model.response.WorkTypeDto
import com.finalworksystem.domain.work.model.WorkType as DomainWorkType

data class WorkType(
    val id: Int,
    val name: String,
    val description: String?
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
