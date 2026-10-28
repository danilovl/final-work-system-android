package com.finalworksystem.data.task.model.response

import com.finalworksystem.data.task.model.Task
import com.finalworksystem.data.task.model.WorkInfo
import com.finalworksystem.data.task.model.toDomainModel
import com.finalworksystem.data.work.model.response.WorkResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

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

private data class TaskResponseDelegateDto(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("active")
    val active: Boolean?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("complete")
    val complete: Boolean?,

    @SerializedName("notifyComplete")
    val notifyComplete: Boolean?,

    @SerializedName("deadline")
    val deadline: String?,

    @SerializedName("work")
    val work: WorkResponse?
)

class TaskResponseAdapter : JsonDeserializer<TaskResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): TaskResponse? {
        if (json == null || !json.isJsonObject) {
            return null
        }
        val jsonObject = json.asJsonObject
        val targetObject = when {
            jsonObject.has("result") && jsonObject.get("result").isJsonObject -> jsonObject.getAsJsonObject("result")
            jsonObject.has("task") && jsonObject.get("task").isJsonObject -> jsonObject.getAsJsonObject("task")
            jsonObject.has("data") && jsonObject.get("data").isJsonObject -> jsonObject.getAsJsonObject("data")
            else -> jsonObject
        }
        val dto = context?.deserialize<TaskResponseDelegateDto>(targetObject, TaskResponseDelegateDto::class.java) ?: return null

        return TaskResponse(
            id = dto.id,
            active = dto.active,
            name = dto.name,
            description = dto.description,
            complete = dto.complete,
            notifyComplete = dto.notifyComplete,
            deadline = dto.deadline,
            work = dto.work
        )
    }
}

@JsonAdapter(TaskResponseAdapter::class)
data class TaskResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("active")
    val active: Boolean?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("complete")
    val complete: Boolean?,

    @SerializedName("notifyComplete")
    val notifyComplete: Boolean?,

    @SerializedName("deadline")
    val deadline: String?,

    @SerializedName("work")
    val work: WorkResponse?
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

