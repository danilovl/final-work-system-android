package com.finalworksystem.data.work.model

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.user.model.toDomainModel
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
