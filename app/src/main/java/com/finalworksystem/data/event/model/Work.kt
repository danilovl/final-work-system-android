package com.finalworksystem.data.event.model

import com.finalworksystem.data.event.model.response.WorkResponse
import com.finalworksystem.data.event.model.response.toDataModel
import com.finalworksystem.domain.work.model.Work as DomainWork

data class Work(
    val id: Int,
    val title: String,
    val shortcut: String?,
    val type: WorkType,
    val status: WorkStatus,
    val deadline: String,
    val deadlineProgram: String?,
    val author: User?,
    val supervisor: User?,
    val opponent: User?,
    val consultant: User?
)

fun WorkResponse.toDataModel(): Work {
    return Work(
        id = id,
        title = title,
        shortcut = shortcut,
        type = type.toDataModel(),
        status = status.toDataModel(),
        deadline = deadline,
        deadlineProgram = deadlineProgram,
        author = author?.toDataModel(),
        supervisor = supervisor?.toDataModel(),
        opponent = opponent?.toDataModel(),
        consultant = consultant?.toDataModel()
    )
}

fun Work.toDomainModel(): DomainWork {
    return DomainWork(
        id = id,
        title = title,
        shortcut = shortcut,
        type = type.toDomainModel(),
        status = status.toDomainModel(),
        deadline = deadline,
        deadlineProgram = deadlineProgram,
        author = author?.toDomainModel(),
        supervisor = supervisor?.toDomainModel(),
        opponent = opponent?.toDomainModel(),
        consultant = consultant?.toDomainModel()
    )
}
