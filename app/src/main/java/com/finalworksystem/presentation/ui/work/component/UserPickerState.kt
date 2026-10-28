package com.finalworksystem.presentation.ui.work.component

import com.finalworksystem.domain.user.model.UserWithWorks

data class UserPickerState(
    val users: List<UserWithWorks> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val totalCount: Int = 0
)
