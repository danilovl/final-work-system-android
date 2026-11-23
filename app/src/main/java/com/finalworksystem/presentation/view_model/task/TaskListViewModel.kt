package com.finalworksystem.presentation.view_model.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.finalworksystem.application.use_case.task.GetTasksForOwnerUseCase
import com.finalworksystem.application.use_case.task.GetTasksForSolverUseCase
import com.finalworksystem.domain.task.model.PaginatedTasks
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.infrastructure.cache.CacheCleanupReason
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.presentation.paging.Page
import com.finalworksystem.presentation.paging.PagedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val getTasksForOwnerUseCase: GetTasksForOwnerUseCase,
    private val getTasksForSolverUseCase: GetTasksForSolverUseCase,
    private val globalCacheManager: GlobalCacheManager
) : ViewModel() {

    enum class TaskListType { OWNER, SOLVER }

    private data class Query(val search: String? = null)

    private val ownerTasks = PagedList<Query, Task>(viewModelScope, PAGE_SIZE) { query, page, pageSize ->
        loadPage(query, page, pageSize) { p, limit -> getTasksForOwnerUseCase(page = p, limit = limit).first() }
    }

    private val solverTasks = PagedList<Query, Task>(viewModelScope, PAGE_SIZE) { query, page, pageSize ->
        loadPage(query, page, pageSize) { p, limit -> getTasksForSolverUseCase(page = p, limit = limit).first() }
    }

    /**
     * Without a search the API pages are shown as they are. The task list API does not support
     * searching, so with a search all tasks are fetched and filtered here, returned as one page.
     */
    private suspend fun loadPage(
        query: Query,
        page: Int,
        pageSize: Int,
        fetch: suspend (page: Int, limit: Int) -> Result<PaginatedTasks>
    ): Result<Page<Task>> {
        val search = query.search
            ?: return fetch(page, pageSize).map { Page(it.tasks, it.totalCount) }

        // Everything is returned with the first page, so there is nothing after it.
        if (page > 1) return Result.success(Page(emptyList(), 0))

        val all = LinkedHashMap<Int, Task>()
        for (apiPage in 1..SEARCH_MAX_PAGES) {
            val result = fetch(apiPage, SEARCH_PAGE_SIZE).getOrElse { return Result.failure(it) }
            val sizeBefore = all.size
            result.tasks.forEach { all.putIfAbsent(it.id, it) }
            if (all.size == sizeBefore) break
            val reachedTotal = result.totalCount > 0 && all.size >= result.totalCount
            // The server may cap the page size, so only an empty page (or the total) ends the list.
            if (result.tasks.isEmpty() || reachedTotal) break
        }

        val matches = all.values.filter { it.matches(search) }
        return Result.success(Page(matches, matches.size))
    }

    private fun Task.matches(search: String): Boolean {
        val words = search.lowercase().split(' ').filter { it.isNotBlank() }
        val haystack = listOfNotNull(name, description, work?.title, work?.shortcut)
            .joinToString(" ")
            .lowercase()
        return words.all { it in haystack }
    }

    private val searchQueries = mapOf(
        TaskListType.OWNER to MutableStateFlow(""),
        TaskListType.SOLVER to MutableStateFlow("")
    )

    init {
        viewModelScope.launch {
            globalCacheManager.cacheCleanupEvents.collect { event ->
                if (event.reason == CacheCleanupReason.DATA_UPDATED) {
                    // A task was created or changed: reload the lists that are shown.
                    ownerTasks.refresh()
                    solverTasks.refresh()
                } else {
                    ownerTasks.reset()
                    solverTasks.reset()
                }
            }
        }
    }

    fun tasks(type: TaskListType): Flow<PagingData<Task>> = list(type).items

    fun totalCount(type: TaskListType): StateFlow<Int> = list(type).totalCount

    private val searchQueryStates: Map<TaskListType, StateFlow<String>> =
        searchQueries.mapValues { it.value.asStateFlow() }

    fun searchQuery(type: TaskListType): StateFlow<String> = searchQueryStates.getValue(type)

    /** Shows the tasks; they change often, so an already shown list is fetched again. */
    fun loadTasks(type: TaskListType) {
        list(type).reload(currentQuery(type))
    }

    fun refresh(type: TaskListType) {
        list(type).refresh()
    }

    fun updateSearchQuery(type: TaskListType, query: String) {
        searchFlow(type).value = query
    }

    fun performSearch(type: TaskListType, query: String) {
        searchFlow(type).value = query
        list(type).load(currentQuery(type))
    }

    fun clearSearch(type: TaskListType) {
        performSearch(type, "")
    }

    private fun currentQuery(type: TaskListType) = Query(searchFlow(type).value.trim().ifBlank { null })

    private fun searchFlow(type: TaskListType) = searchQueries.getValue(type)

    private fun list(type: TaskListType) = when (type) {
        TaskListType.OWNER -> ownerTasks
        TaskListType.SOLVER -> solverTasks
    }

    private companion object {
        const val PAGE_SIZE = 10
        const val SEARCH_PAGE_SIZE = 50
        const val SEARCH_MAX_PAGES = 40
    }
}
