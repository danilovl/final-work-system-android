@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.task.model.response

import com.finalworksystem.data.task.model.Task
import com.finalworksystem.data.task.model.WorkInfo
import com.finalworksystem.data.task.model.toDomainModel
import com.finalworksystem.data.work.model.response.WorkResponse
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import com.finalworksystem.infrastructure.api.serialization.UnwrappingSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class TaskListResponse(
    @SerialName("numItemsPerPage")
    val numItemsPerPage: Int = 0,

    @SerialName("totalCount")
    val totalCount: Int = 0,

    @SerialName("currentItemCount")
    val currentItemCount: Int = 0,

    @SerialName("result")
    val result: List<TaskResponse> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = TaskResponseSerializer::class)
data class TaskResponse(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("active")
    val active: Boolean? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("complete")
    val complete: Boolean? = null,

    @SerialName("notifyComplete")
    val notifyComplete: Boolean? = null,

    @SerialName("deadline")
    val deadline: String? = null,

    @SerialName("work")
    val work: WorkResponse? = null
)

/** Accepts the object either directly or wrapped in one of listOf("result", "task", "data"). */
object TaskResponseSerializer : UnwrappingSerializer<TaskResponse>(
    TaskResponse.generatedSerializer(),
    listOf("result", "task", "data")
)

fun TaskResponse.toDataModel(): Task {
    return Task(
        id = id ?: 0,
        active = active ?: false,
        name = name ?: "",
        description = description,
        complete = complete ?: false,
        notifyComplete = notifyComplete ?: false,
        deadline = deadline,
        work = work?.toDataModel()
    )
}

fun WorkResponse?.toDataModel(): WorkInfo? {
    if (this == null) {
        return null
    }

    return WorkInfo(
        id = id ?: 0,
        title = title ?: "",
        shortcut = shortcut,
        deadline = deadline ?: "",
        deadlineProgram = deadlineProgram
    )
}

fun TaskListResponse.toDataModel(): List<Task> {
    return result.map { it.toDataModel() }
}

fun TaskListResponse.toPaginatedDomainModel(): com.finalworksystem.domain.task.model.PaginatedTasks {
    val tasks = result.map { it.toDataModel().toDomainModel() }

    return com.finalworksystem.domain.task.model.PaginatedTasks(
        tasks = tasks,
        totalCount = totalCount,
        currentItemCount = currentItemCount,
        numItemsPerPage = numItemsPerPage
    )
}

