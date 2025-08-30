package com.finalworksystem.data.task.model

import com.finalworksystem.domain.task.model.Task as DomainTask
import com.finalworksystem.domain.task.model.WorkInfo as DomainWorkInfo

data class Task(
    val id: Int,
    val active: Boolean,
    val name: String,
    val description: String?,
    val complete: Boolean,
    val notifyComplete: Boolean,
    val deadline: String?,
    val work: WorkInfo?
)

data class WorkInfo(
    val id: Int,
    val title: String,
    val shortcut: String?,
    val deadline: String,
    val deadlineProgram: String?
)

fun Task.toDomainModel(): DomainTask {
    return DomainTask(
        id = id,
        active = active,
        name = name,
        description = description,
        complete = complete,
        notifyComplete = notifyComplete,
        deadline = deadline,
        work = work?.toDomainModel()
    )
}

fun WorkInfo.toDomainModel(): DomainWorkInfo {
    return DomainWorkInfo(
        id = id,
        title = title,
        shortcut = shortcut,
        deadline = deadline,
        deadlineProgram = deadlineProgram
    )
}
