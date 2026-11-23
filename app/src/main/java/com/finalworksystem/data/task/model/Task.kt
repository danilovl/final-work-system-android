@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.task.model

import com.finalworksystem.domain.task.model.Task as DomainTask
import com.finalworksystem.domain.task.model.WorkInfo as DomainWorkInfo
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Task(
    val id: Int = 0,
    val active: Boolean = false,
    val name: String = "",
    val description: String? = null,
    val complete: Boolean = false,
    val notifyComplete: Boolean = false,
    val deadline: String? = null,
    val work: WorkInfo? = null
)

@Serializable
data class WorkInfo(
    val id: Int = 0,
    val title: String = "",
    val shortcut: String? = null,
    val deadline: String = "",
    val deadlineProgram: String? = null
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
