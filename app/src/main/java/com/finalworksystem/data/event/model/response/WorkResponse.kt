package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.conversation.model.response.WorkStatusDto
import com.finalworksystem.data.conversation.model.response.WorkTypeDto
import com.finalworksystem.data.event.model.Work
import com.finalworksystem.data.event.model.toDataModel
import com.google.gson.annotations.SerializedName

data class WorkResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("shortcut")
    val shortcut: String?,

    @SerializedName("type")
    val type: WorkTypeDto,

    @SerializedName("status")
    val status: WorkStatusDto,

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

fun WorkResponse.toDataModel(): Work {
    return Work(
        id = id,
        title = title,
        shortcut = shortcut,
        type = type.toDataModel(),
        status = status.toDataModel(),
        deadline = deadline,
        deadlineProgram = deadlineProgram,
        author = author?.toDataModel(),
        supervisor = supervisor?.toDataModel(),
        opponent = opponent?.toDataModel(),
        consultant = consultant?.toDataModel()
    )
}
