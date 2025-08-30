package com.finalworksystem.domain.conversation.model

import com.finalworksystem.domain.user.model.User

data class ConversationWork(
    val id: Int,
    val title: String,
    val shortcut: String?,
    val deadline: String,
    val deadlineProgram: String?,
    val status: WorkStatus,
    val type: WorkType,
    val author: User?,
    val supervisor: User?,
    val opponent: User?,
    val consultant: User?
)
