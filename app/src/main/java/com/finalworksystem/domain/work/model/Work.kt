package com.finalworksystem.domain.work.model

import com.finalworksystem.domain.user.model.User

data class Work(
    val id: Int,
    val title: String,
    val shortcut: String?,
    val type: WorkType,
    val status: WorkStatus,
    val deadline: String,
    val deadlineProgram: String?,
    val author: User?,
    val supervisor: User?,
    val opponent: User?,
    val consultant: User?
)

data class PaginatedWorks(
    val works: List<Work>,
    val totalCount: Int,
    val currentItemCount: Int,
    val numItemsPerPage: Int
)
