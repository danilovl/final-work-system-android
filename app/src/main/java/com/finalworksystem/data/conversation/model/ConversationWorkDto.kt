package com.finalworksystem.data.conversation.model

import com.finalworksystem.data.conversation.model.response.WorkStatusDto
import com.finalworksystem.data.conversation.model.response.WorkTypeDto
import com.finalworksystem.data.conversation.model.response.toDomain

data class ConversationWorkDto(
    val id: Int,
    val title: String,
    val shortcut: String?,
    val deadline: String,
    val deadlineProgram: String?,
    val status: WorkStatusDto,
    val type: WorkTypeDto,
    val author: ConversationUserDto?,
    val supervisor: ConversationUserDto?,
    val opponent: ConversationUserDto?,
    val consultant: ConversationUserDto?
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
