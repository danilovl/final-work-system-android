package com.finalworksystem.presentation.view_model.work

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.work.GetWorkListWithPaginationUseCase
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkListViewModel(
    application: Application,
    private val getWorkListWithPaginationUseCase: GetWorkListWithPaginationUseCase,
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

    private var hasLeftWorkListSection = false

    private var cachedWorks: Map<WorkListType, List<Work>> = mapOf()

    private var currentPage = 1
    private val pageSize = 20
    private var currentType = WorkListType.AUTHOR
    private var currentSearch: String? = null
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
    }

    fun loadWorks(type: WorkListType = WorkListType.AUTHOR, forceRefresh: Boolean = false, search: String? = null) {
        if (!forceRefresh && !hasLeftWorkListSection && cachedWorks.containsKey(type) && search == null) {
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
            currentSearch = search

            getWorkListWithPaginationUseCase(type, currentPage, pageSize, search).collect { result ->
                result.fold(
                    onSuccess = { paginatedWorks ->
                        val works = paginatedWorks.works
                        val hasMore = paginatedWorks.currentItemCount + ((currentPage - 1) * pageSize) < paginatedWorks.totalCount

                        cachedWorks = cachedWorks + (type to works)
                        hasLeftWorkListSection = false

                        _worksState.value = WorksState.Success(works, hasMore, false, paginatedWorks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _worksState.value = WorksState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
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

            getWorkListWithPaginationUseCase(currentType, currentPage, pageSize, currentSearch).collect { result ->
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
                        val errorMessage = error.message ?: "Unknown error"
                        _worksState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage("Failed to load more works: $errorMessage", PopupMessageService.MessageLevel.ERROR)
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

        if (_searchQuery.value.isNotBlank() || wasErrorState) {
            loadWorks(currentType, forceRefresh = true, search = _searchQuery.value.takeIf { it.isNotBlank() })
        }
    }

    fun clearWorkCache() {
        cachedWorks = mapOf()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun performSearch(query: String) {
        loadWorks(currentType, forceRefresh = true, search = query.takeIf { it.isNotBlank() })
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadWorks(currentType, forceRefresh = true, search = null)
    }
}