@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.work.model.response

import com.finalworksystem.data.user.model.response.UserResponse
import com.finalworksystem.data.user.model.response.toDataModel
import com.finalworksystem.data.work.model.Work
import com.finalworksystem.data.work.model.WorkStatus
import com.finalworksystem.data.work.model.WorkType
import com.finalworksystem.data.work.model.toDomainModel
import com.finalworksystem.domain.work.model.Work as DomainWork
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import com.finalworksystem.infrastructure.api.serialization.UnwrappingSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = WorkResponseSerializer::class)
data class WorkResponse(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("shortcut")
    val shortcut: String? = null,

    @SerialName("type")
    val type: TypeResponse? = null,

    @SerialName("status")
    val status: StatusResponse? = null,

    @SerialName("deadline")
    val deadline: String? = null,

    @SerialName("deadlineProgram")
    val deadlineProgram: String? = null,

    @SerialName("author")
    val author: UserResponse? = null,

    @SerialName("supervisor")
    val supervisor: UserResponse? = null,

    @SerialName("opponent")
    val opponent: UserResponse? = null,

    @SerialName("consultant")
    val consultant: UserResponse? = null
)

/** Accepts the object either directly or wrapped in one of listOf("result", "work", "data"). */
object WorkResponseSerializer : UnwrappingSerializer<WorkResponse>(
    WorkResponse.generatedSerializer(),
    listOf("result", "work", "data")
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
