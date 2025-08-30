package com.finalworksystem.domain.task.model

data class PaginatedTasks(
    val tasks: List<Task>,
    val totalCount: Int,
    val currentItemCount: Int,
    val numItemsPerPage: Int
)