package com.finalworksystem.data.task.model.response

import com.finalworksystem.data.task.model.Task
import com.finalworksystem.data.task.model.WorkInfo
import com.finalworksystem.data.task.model.toDomainModel
import com.finalworksystem.data.work.model.response.WorkResponse
import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("numItemsPerPage")
    val numItemsPerPage: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("currentItemCount")
    val currentItemCount: Int,

    @SerializedName("result")
    val result: List<TaskResponse>
)

data class TaskResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("active")
    val active: Boolean,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("complete")
    val complete: Boolean,

    @SerializedName("notifyComplete")
    val notifyComplete: Boolean,

    @SerializedName("deadline")
    val deadline: String?,

    @SerializedName("work")
    val work: WorkResponse?
)

fun TaskResponse.toDataModel(): Task {
    return Task(
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

fun WorkResponse.toDataModel(): WorkInfo {
    return WorkInfo(
        id = id,
        title = title,
        shortcut = null,
        deadline = deadline,
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

