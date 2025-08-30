package com.finalworksystem.data.work.model.response

import com.finalworksystem.data.user.model.User
import com.finalworksystem.data.work.model.Work
import com.finalworksystem.data.work.model.WorkStatus
import com.finalworksystem.data.work.model.WorkType
import com.finalworksystem.data.work.model.toDomainModel
import com.google.gson.annotations.SerializedName
import com.finalworksystem.domain.work.model.Work as DomainWork

data class WorkResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("shortcut")
    val shortcut: String?,

    @SerializedName("type")
    val type: TypeResponse,

    @SerializedName("status")
    val status: StatusResponse,

    @SerializedName("deadline")
    val deadline: String,

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

fun WorkResponse.toDataModel(): Work {
    return Work(
        id = id,
        title = title,
        shortcut = shortcut,
        type = type.toDataModel().let { WorkType(it.id, it.name, it.description) },
        status = status.toDataModel().let { WorkStatus(it.id, it.name, it.description) },
        deadline = deadline,
        deadlineProgram = deadlineProgram,
        author = author?.toDataModel()?.let {
            User(
                it.id,
                it.username,
                it.firstname,
                it.lastname,
                it.fullName,
                it.degreeBefore,
                it.degreeAfter,
                it.email,
                it.token ?: "",
                it.roles
            )
        },
        supervisor = supervisor?.toDataModel()?.let {
            User(
                it.id,
                it.username,
                it.firstname,
                it.lastname,
                it.fullName,
                it.degreeBefore,
                it.degreeAfter,
                it.email,
                it.token ?: "",
                it.roles
            )
        },
        opponent = opponent?.toDataModel()?.let {
            User(
                it.id,
                it.username,
                it.firstname,
                it.lastname,
                it.fullName,
                it.degreeBefore,
                it.degreeAfter,
                it.email,
                it.token ?: "",
                it.roles
            )
        },
        consultant = consultant?.toDataModel()?.let {
            User(
                it.id,
                it.username,
                it.firstname,
                it.lastname,
                it.fullName,
                it.degreeBefore,
                it.degreeAfter,
                it.email,
                it.token ?: "",
                it.roles
            )
        }
    )
}

fun WorkResponse.toDomainModel(): DomainWork {
    return this.toDataModel().toDomainModel()
}
