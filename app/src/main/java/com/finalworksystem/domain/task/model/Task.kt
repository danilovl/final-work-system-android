package com.finalworksystem.domain.task.model

import com.finalworksystem.data.task.model.Task as DataTask

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

fun Task?.toDataModel(): DataTask {
    requireNotNull(this) { "Task cannot be null" }
    return DataTask(
        id = id,
        active = active,
        name = name,
        description = description,
        complete = complete,
        notifyComplete = notifyComplete,
        deadline = deadline,
        work = work?.toDataModel()
    )
}
