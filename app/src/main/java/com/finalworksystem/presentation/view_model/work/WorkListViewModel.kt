package com.finalworksystem.presentation.view_model.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.finalworksystem.application.use_case.user.GetUserListUseCase
import com.finalworksystem.application.use_case.work.GetWorkListWithPaginationUseCase
import com.finalworksystem.application.use_case.work.GetWorkStatusesUseCase
import com.finalworksystem.application.use_case.work.GetWorkTypesUseCase
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkFilter
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.model.WorkType
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.user.UserService
import com.finalworksystem.presentation.paging.Page
import com.finalworksystem.presentation.paging.PagedList
import com.finalworksystem.presentation.paging.UserPicker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WorkListViewModel(
    private val getWorkListWithPaginationUseCase: GetWorkListWithPaginationUseCase,
    private val getWorkTypesUseCase: GetWorkTypesUseCase,
    private val getWorkStatusesUseCase: GetWorkStatusesUseCase,
    getUserListUseCase: GetUserListUseCase,
    userService: UserService,
    globalCacheManager: GlobalCacheManager
) : ViewModel() {

    data class Query(
        val type: WorkListType,
        val filter: WorkFilter
    )

    private val pagedWorks = PagedList<Query, Work>(viewModelScope, PAGE_SIZE) { query, page, pageSize ->
        getWorkListWithPaginationUseCase(query.type, page, pageSize, query.filter).first()
            .map { Page(it.works, it.totalCount) }
    }

    val works: Flow<PagingData<Work>> = pagedWorks.items

    val totalCount: StateFlow<Int> = pagedWorks.totalCount

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(WorkFilter())
    val filter: StateFlow<WorkFilter> = _filter.asStateFlow()

    private val _workTypes = MutableStateFlow<List<WorkType>>(emptyList())
    val workTypes: StateFlow<List<WorkType>> = _workTypes.asStateFlow()

    private val _workStatuses = MutableStateFlow<List<WorkStatus>>(emptyList())
    val workStatuses: StateFlow<List<WorkStatus>> = _workStatuses.asStateFlow()

    /** Searchable paged user lists for the author / opponent filter dialogs. */
    val userPicker = UserPicker(viewModelScope, getUserListUseCase)

    val selectedAuthor = MutableStateFlow<User?>(null)
    val selectedOpponent = MutableStateFlow<User?>(null)

    private var currentType = WorkListType.AUTHOR

    init {
        // Works of one user must never be shown to another one.
        viewModelScope.launch {
            userService.getUserFlowSafe()
                .map { it?.id }
                .distinctUntilChanged()
                .drop(1)
                .collect { pagedWorks.reset() }
        }

        viewModelScope.launch {
            globalCacheManager.cacheCleanupEvents.collect { pagedWorks.reset() }
        }

        loadFilterOptions()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            getWorkTypesUseCase().collect { result ->
                result.onSuccess { _workTypes.value = it }
            }
        }
        viewModelScope.launch {
            getWorkStatusesUseCase().collect { result ->
                result.onSuccess { _workStatuses.value = it }
            }
        }
    }

    /** Shows the works of the type with the current filter; loaded pages are kept. */
    fun loadWorks(type: WorkListType = WorkListType.AUTHOR) {
        currentType = type
        pagedWorks.load(Query(type, _filter.value))
    }

    fun refresh() {
        pagedWorks.refresh()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _filter.value = _filter.value.copy(title = query)
    }

    fun performSearch(query: String) {
        applyFilter(_filter.value.copy(title = query))
    }

    fun updateFilter(newFilter: WorkFilter) {
        applyFilter(newFilter)
    }

    fun clearSearch() {
        applyFilter(_filter.value.copy(title = null))
    }

    fun clearFilter() {
        selectedAuthor.value = null
        selectedOpponent.value = null
        applyFilter(WorkFilter())
    }

    private fun applyFilter(filter: WorkFilter) {
        _filter.value = filter
        _searchQuery.value = filter.title.orEmpty()
        pagedWorks.load(Query(currentType, filter))
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
