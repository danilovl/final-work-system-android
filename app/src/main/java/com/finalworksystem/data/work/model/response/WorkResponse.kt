package com.finalworksystem.data.work.model.response

import com.finalworksystem.data.user.model.response.UserResponse
import com.finalworksystem.data.user.model.response.toDataModel
import com.finalworksystem.data.work.model.Work
import com.finalworksystem.data.work.model.WorkStatus
import com.finalworksystem.data.work.model.WorkType
import com.finalworksystem.data.work.model.toDomainModel
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import com.finalworksystem.domain.work.model.Work as DomainWork

private data class WorkResponseDelegateDto(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("shortcut")
    val shortcut: String?,

    @SerializedName("type")
    val type: TypeResponse?,

    @SerializedName("status")
    val status: StatusResponse?,

    @SerializedName("deadline")
    val deadline: String?,

    @SerializedName("deadlineProgram")
    val deadlineProgram: String?,

    @SerializedName("author")
    val author: UserResponse?,

    @SerializedName("supervisor")
    val supervisor: UserResponse?,

    @SerializedName("opponent")
    val opponent: UserResponse?,

    @SerializedName("consultant")
    val consultant: UserResponse?
)

class WorkResponseAdapter : JsonDeserializer<WorkResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WorkResponse? {
        if (json == null || !json.isJsonObject) {
            return null
        }
        val jsonObject = json.asJsonObject
        val targetObject = when {
            jsonObject.has("result") && jsonObject.get("result").isJsonObject -> jsonObject.getAsJsonObject("result")
            jsonObject.has("work") && jsonObject.get("work").isJsonObject -> jsonObject.getAsJsonObject("work")
            jsonObject.has("data") && jsonObject.get("data").isJsonObject -> jsonObject.getAsJsonObject("data")
            else -> jsonObject
        }
        val dto = context?.deserialize<WorkResponseDelegateDto>(targetObject, WorkResponseDelegateDto::class.java) ?: return null

        return WorkResponse(
            id = dto.id,
            title = dto.title,
            shortcut = dto.shortcut,
            type = dto.type,
            status = dto.status,
            deadline = dto.deadline,
            deadlineProgram = dto.deadlineProgram,
            author = dto.author,
            supervisor = dto.supervisor,
            opponent = dto.opponent,
            consultant = dto.consultant
        )
    }
}

@JsonAdapter(WorkResponseAdapter::class)
data class WorkResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("shortcut")
    val shortcut: String?,

    @SerializedName("type")
    val type: TypeResponse?,

    @SerializedName("status")
    val status: StatusResponse?,

    @SerializedName("deadline")
    val deadline: String?,

    @SerializedName("deadlineProgram")
    val deadlineProgram: String?,

    @SerializedName("author")
    val author: UserResponse?,

    @SerializedName("supervisor")
    val supervisor: UserResponse?,

    @SerializedName("opponent")
    val opponent: UserResponse?,

    @SerializedName("consultant")
    val consultant: UserResponse?
)

typealias WorkDetailResponse = WorkResponse

fun WorkResponse?.toDataModel(): Work {
    if (this == null) {
        return Work(
            id = 0,
            title = "",
            shortcut = null,
            type = WorkType(0, "", null),
            status = WorkStatus(0, "", null),
            deadline = "",
            deadlineProgram = null,
            author = null,
            supervisor = null,
            opponent = null,
            consultant = null
        )
    }
    return Work(
        id = id ?: 0,
        title = title ?: "",
        shortcut = shortcut,
        type = type?.toDataModel()?.let { WorkType(it.id, it.name, it.description) } ?: WorkType(0, "", null),
        status = status?.toDataModel()?.let { WorkStatus(it.id, it.name, it.description) } ?: WorkStatus(0, "", null),
        deadline = deadline ?: "",
        deadlineProgram = deadlineProgram,
        author = author?.toDataModel(),
        supervisor = supervisor?.toDataModel(),
        opponent = opponent?.toDataModel(),
        consultant = consultant?.toDataModel()
    )
}

fun WorkResponse?.toDomainModel(): DomainWork {
    return this.toDataModel().toDomainModel()
}
