package com.finalworksystem.presentation.view_model.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.finalworksystem.application.use_case.user.GetUserListUseCase
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.user.model.UserWithWorks
import com.finalworksystem.presentation.paging.Page
import com.finalworksystem.presentation.paging.PagedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class UserViewModel(
    private val getUserListUseCase: GetUserListUseCase
) : ViewModel() {

    private data class Query(val type: UserType, val search: String? = null)

    private val pagedUsers = PagedList<Query, UserWithWorks>(viewModelScope, PAGE_SIZE) { query, page, pageSize ->
        val search = query.search
        if (search == null) {
            getUserListUseCase(query.type, page, pageSize).first()
                .map { Page(it.result, it.totalCount) }
        } else {
            searchUsers(query.type, search, page)
        }
    }

    /**
     * The search endpoint returns users without their works, so the search runs over the
     * regular list (which includes works): all users are fetched, filtered and returned as one page.
     */
    private suspend fun searchUsers(type: UserType, search: String, page: Int): Result<Page<UserWithWorks>> {
        if (page > 1) return Result.success(Page(emptyList(), 0))

        val all = LinkedHashMap<Int, UserWithWorks>()
        for (apiPage in 1..SEARCH_MAX_PAGES) {
            val result = getUserListUseCase(type, apiPage, SEARCH_PAGE_SIZE).first()
                .getOrElse { return Result.failure(it) }
            val sizeBefore = all.size
            result.result.forEach { all.putIfAbsent(it.user.id, it) }
            val reachedTotal = result.totalCount > 0 && all.size >= result.totalCount
            if (all.size == sizeBefore || reachedTotal) break
        }

        val words = search.lowercase().split(' ').filter { it.isNotBlank() }
        val matches = all.values.filter { item ->
            val user = item.user
            val haystack = listOfNotNull(user.fullName, user.firstname, user.lastname, user.username, user.email)
                .joinToString(" ")
                .lowercase()
            words.all { it in haystack }
        }
        return Result.success(Page(matches, matches.size))
    }

    val users: Flow<PagingData<UserWithWorks>> = pagedUsers.items

    val totalCount: StateFlow<Int> = pagedUsers.totalCount

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun loadUserList(type: UserType) {
        // The search belongs to one list type; switching to another type starts without it.
        if (pagedUsers.query?.type != type) {
            _searchQuery.value = ""
        }
        pagedUsers.reload(currentQuery(type))
    }

    fun refresh() {
        pagedUsers.refresh()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun performSearch(query: String) {
        val type = pagedUsers.query?.type ?: return
        _searchQuery.value = query
        pagedUsers.load(currentQuery(type))
    }

    fun clearSearch() {
        performSearch("")
    }

    private fun currentQuery(type: UserType) = Query(type, _searchQuery.value.trim().ifBlank { null })

    private companion object {
        const val PAGE_SIZE = 20
        const val SEARCH_PAGE_SIZE = 50
        const val SEARCH_MAX_PAGES = 40
    }
}
