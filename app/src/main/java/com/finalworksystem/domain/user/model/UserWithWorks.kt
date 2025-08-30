package com.finalworksystem.domain.user.model

import com.finalworksystem.domain.work.model.Work

data class UserWithWorks(
    val user: User,
    val works: List<Work>
)

data class UserListResult(
    val numItemsPerPage: Int,
    val totalCount: Int,
    val currentItemCount: Int,
    val result: List<UserWithWorks>
)