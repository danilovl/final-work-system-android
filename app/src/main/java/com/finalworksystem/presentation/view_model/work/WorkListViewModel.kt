package com.finalworksystem.presentation.view_model.work

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.user.GetUserListUseCase
import com.finalworksystem.application.use_case.work.GetWorkListWithPaginationUseCase
import com.finalworksystem.application.use_case.work.GetWorkStatusesUseCase
import com.finalworksystem.application.use_case.work.GetWorkTypesUseCase
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.domain.user.model.UserWithWorks
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkFilter
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.model.WorkType
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.presentation.ui.work.component.UserPickerState
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkListViewModel(
    application: Application,
    private val getWorkListWithPaginationUseCase: GetWorkListWithPaginationUseCase,
    private val getWorkTypesUseCase: GetWorkTypesUseCase,
    private val getWorkStatusesUseCase: GetWorkStatusesUseCase,
    private val getUserListUseCase: GetUserListUseCase,
    private val popupMessageService: PopupMessageService,
    private val userService: UserService,
    private val globalCacheManager: GlobalCacheManager
) : AndroidViewModel(application) {

    sealed class WorksState {
        object Idle : WorksState()
        object Loading : WorksState()
        data class Success(
            val works: List<Work>,
            val hasMoreWorks: Boolean,
            val isLoadingMore: Boolean,
            val totalCount: Int
        ) : WorksState()
        data class Error(val message: String) : WorksState()
    }

    private val _worksState = MutableStateFlow<WorksState>(WorksState.Idle)
    val worksState: StateFlow<WorksState> = _worksState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(WorkFilter())
    val filter: StateFlow<WorkFilter> = _filter.asStateFlow()

    private val _workTypes = MutableStateFlow<List<WorkType>>(emptyList())
    val workTypes: StateFlow<List<WorkType>> = _workTypes.asStateFlow()

    private val _workStatuses = MutableStateFlow<List<WorkStatus>>(emptyList())
    val workStatuses: StateFlow<List<WorkStatus>> = _workStatuses.asStateFlow()

    private val _userPickersState = MutableStateFlow<Map<UserType, UserPickerState>>(
        mapOf(
            UserType.AUTHOR to UserPickerState(),
            UserType.OPPONENT to UserPickerState()
        )
    )
    val userPickersState: StateFlow<Map<UserType, UserPickerState>> = _userPickersState.asStateFlow()

    val selectedAuthor = MutableStateFlow<User?>(null)
    val selectedOpponent = MutableStateFlow<User?>(null)

    private var hasLeftWorkListSection = false

    private var cachedWorks: Map<WorkListType, List<Work>> = mapOf()

    private var currentPage = 1
    private val pageSize = 20
    private var currentType = WorkListType.AUTHOR
    private var isLoadingMore = false

    private var currentUserId: Int? = null

    init {
        viewModelScope.launch {
            userService.getUserFlowSafe().collect { user ->
                val newUserId = user?.id
                if (currentUserId != null && currentUserId != newUserId) {
                    clearWorkCache()
                }
                currentUserId = newUserId
            }
        }

        viewModelScope.launch {
            globalCacheManager.cacheCleanupEvents.collect { cacheCleanupEvent ->
                clearWorkCache()
                popupMessageService.showMessage(
                    "Cache cleared due to ${cacheCleanupEvent.reason.name.lowercase()}", 
                    PopupMessageService.MessageLevel.INFO
                )
            }
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

    fun loadWorks(type: WorkListType = WorkListType.AUTHOR, forceRefresh: Boolean = false, filter: WorkFilter? = null) {
        val effectiveFilter = filter ?: _filter.value
        
        if (!forceRefresh && !hasLeftWorkListSection && cachedWorks.containsKey(type) && effectiveFilter.isEmpty()) {
            val cachedWorksList = cachedWorks[type]
            if (cachedWorksList != null && cachedWorksList.isNotEmpty()) {
                _worksState.value = WorksState.Success(cachedWorksList, false, false, cachedWorksList.size)
                return
            }
        }

        viewModelScope.launch {
            _worksState.value = WorksState.Loading
            currentPage = 1
            currentType = type
            _filter.value = effectiveFilter
            _searchQuery.value = effectiveFilter.title ?: ""

            getWorkListWithPaginationUseCase(type, currentPage, pageSize, effectiveFilter).collect { result ->
                result.fold(
                    onSuccess = { paginatedWorks ->
                        val works = paginatedWorks.works
                        val hasMore = paginatedWorks.currentItemCount + ((currentPage - 1) * pageSize) < paginatedWorks.totalCount

                        cachedWorks = cachedWorks + (type to works)
                        hasLeftWorkListSection = false

                        _worksState.value = WorksState.Success(works, hasMore, false, paginatedWorks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message
                        if (errorMessage != null) {
                            _worksState.value = WorksState.Error(errorMessage)
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        } else {
                            val fallbackMessage = getApplication<Application>().getString(com.finalworksystem.R.string.unknown_error)
                            _worksState.value = WorksState.Error(fallbackMessage)
                            popupMessageService.showMessageResource(com.finalworksystem.R.string.unknown_error, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    fun loadMoreWorks() {
        val currentState = _worksState.value
        if (currentState !is WorksState.Success || !currentState.hasMoreWorks || isLoadingMore || currentState.isLoadingMore) {
            return
        }

        isLoadingMore = true
        _worksState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            currentPage++

            getWorkListWithPaginationUseCase(currentType, currentPage, pageSize, _filter.value).collect { result ->
                isLoadingMore = false
                result.fold(
                    onSuccess = { paginatedWorks ->
                        val newWorks = paginatedWorks.works
                        val currentWorks = currentState.works
                        val updatedWorks = currentWorks + newWorks
                        val hasMore = updatedWorks.size < paginatedWorks.totalCount

                        _worksState.value = WorksState.Success(updatedWorks, hasMore, false, paginatedWorks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message
                        _worksState.value = currentState.copy(isLoadingMore = false)
                        if (errorMessage != null) {
                            popupMessageService.showMessage("Failed to load more works: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                        } else {
                            popupMessageService.showMessageResource(com.finalworksystem.R.string.unknown_error, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    fun markLeftWorkListSection() {
        hasLeftWorkListSection = true
    }

    fun markEnteredWorkListSection() {
        hasLeftWorkListSection = false
        val wasErrorState = _worksState.value is WorksState.Error
        if (wasErrorState) {
            _worksState.value = WorksState.Idle
        }

        if (!_filter.value.isEmpty() || wasErrorState) {
            loadWorks(currentType, forceRefresh = true, filter = _filter.value)
        }
    }

    fun clearWorkCache() {
        cachedWorks = mapOf()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _filter.value = _filter.value.copy(title = query)
    }

    fun performSearch(query: String) {
        val newFilter = _filter.value.copy(title = query)
        loadWorks(currentType, forceRefresh = true, filter = newFilter)
    }

    fun updateFilter(newFilter: WorkFilter) {
        loadWorks(currentType, forceRefresh = true, filter = newFilter)
    }

    fun clearSearch() {
        _searchQuery.value = ""
        val newFilter = _filter.value.copy(title = null)
        loadWorks(currentType, forceRefresh = true, filter = newFilter)
    }

    fun clearFilter() {
        _filter.value = WorkFilter()
        _searchQuery.value = ""
        selectedAuthor.value = null
        selectedOpponent.value = null
        loadWorks(currentType, forceRefresh = true, filter = WorkFilter())
    }

    fun loadUsers(type: UserType, query: String = "") {
        viewModelScope.launch {
            val currentState = _userPickersState.value[type] ?: UserPickerState()
            _userPickersState.value = _userPickersState.value + (type to currentState.copy(
                isLoading = true,
                searchQuery = query,
                currentPage = 1
            ))

            getUserListUseCase(type = type, page = 1, pageSize = pageSize, search = query.ifBlank { null }).collect { result ->
                result.fold(
                    onSuccess = { userListResult ->
                        val hasMore = userListResult.currentItemCount < userListResult.totalCount
                        _userPickersState.value = _userPickersState.value + (type to UserPickerState(
                            users = userListResult.result,
                            isLoading = false,
                            isLoadingMore = false,
                            hasMore = hasMore,
                            searchQuery = query,
                            currentPage = 1,
                            totalCount = userListResult.totalCount
                        ))
                    },
                    onFailure = { error ->
                        val msg = error.message ?: "Failed to search users"
                        _userPickersState.value = _userPickersState.value + (type to currentState.copy(
                            isLoading = false
                        ))
                        popupMessageService.showMessage(msg, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreUsers(type: UserType) {
        val currentState = _userPickersState.value[type] ?: return
        if (currentState.isLoading || currentState.isLoadingMore || !currentState.hasMore) return

        viewModelScope.launch {
            val nextPage = currentState.currentPage + 1
            _userPickersState.value = _userPickersState.value + (type to currentState.copy(isLoadingMore = true))

            getUserListUseCase(type = type, page = nextPage, pageSize = pageSize, search = currentState.searchQuery.ifBlank { null }).collect { result ->
                result.fold(
                    onSuccess = { userListResult ->
                        val updatedUsers = currentState.users + userListResult.result
                        val hasMore = updatedUsers.size < userListResult.totalCount
                        _userPickersState.value = _userPickersState.value + (type to currentState.copy(
                            users = updatedUsers,
                            isLoadingMore = false,
                            hasMore = hasMore,
                            currentPage = nextPage,
                            totalCount = userListResult.totalCount
                        ))
                    },
                    onFailure = { error ->
                        val msg = error.message ?: "Failed to load more users"
                        _userPickersState.value = _userPickersState.value + (type to currentState.copy(
                            isLoadingMore = false
                        ))
                        popupMessageService.showMessage(msg, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }
}
