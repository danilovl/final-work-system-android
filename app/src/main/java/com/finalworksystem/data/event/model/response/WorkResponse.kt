@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.event.model.response

import com.finalworksystem.data.conversation.model.response.WorkStatusDto
import com.finalworksystem.data.conversation.model.response.WorkTypeDto
import com.finalworksystem.data.event.model.Work
import com.finalworksystem.data.event.model.toDataModel
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WorkResponse(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("title")
    val title: String = "",

    @SerialName("shortcut")
    val shortcut: String? = null,

    @SerialName("type")
    val type: WorkTypeDto = WorkTypeDto(),
    @SerialName("status")
    val status: WorkStatusDto = WorkStatusDto(),
    @SerialName("deadline")
    val deadline: String = "",

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
