@file:UseSerializers(SafeIntSerializer::class, SafeLongSerializer::class, SafeDoubleSerializer::class)

package com.finalworksystem.data.conversation.model

import com.finalworksystem.data.conversation.model.response.WorkStatusDto
import com.finalworksystem.data.conversation.model.response.WorkTypeDto
import com.finalworksystem.data.conversation.model.response.toDomain
import com.finalworksystem.infrastructure.api.serialization.SafeDoubleSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeIntSerializer
import com.finalworksystem.infrastructure.api.serialization.SafeLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ConversationWorkDto(
    val id: Int = 0,
    val title: String = "",
    val shortcut: String? = null,
    val deadline: String = "",
    val deadlineProgram: String? = null,
    val status: WorkStatusDto = WorkStatusDto(),
    val type: WorkTypeDto = WorkTypeDto(),
    val author: ConversationUserDto? = null,
    val supervisor: ConversationUserDto? = null,
    val opponent: ConversationUserDto? = null,
    val consultant: ConversationUserDto? = null
)

fun ConversationWorkDto.toEntity(): com.finalworksystem.domain.conversation.model.ConversationWork {
    return com.finalworksystem.domain.conversation.model.ConversationWork(
        id = id,
        title = title,
        shortcut = shortcut,
        deadline = deadline,
        deadlineProgram = deadlineProgram,
        status = status.toDomain(),
        type = type.toDomain(),
        author = author?.toEntity(),
        supervisor = supervisor?.toEntity(),
        opponent = opponent?.toEntity(),
        consultant = consultant?.toEntity()
    )
}
