package com.finalworksystem.presentation.view_model.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.task.PostTaskCreateUseCase
import com.finalworksystem.application.use_case.work.GetWorkDetailUseCase
import com.finalworksystem.application.use_case.work.GetWorkListWithPaginationUseCase
import com.finalworksystem.data.task.model.request.TaskCreateRequest
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkListType
import com.finalworksystem.infrastructure.cache.CacheCleanupReason
import com.finalworksystem.infrastructure.cache.GlobalCacheManager
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskCreateViewModel(
    application: Application,
    private val postTaskCreateUseCase: PostTaskCreateUseCase,
    private val getWorkListWithPaginationUseCase: GetWorkListWithPaginationUseCase,
    private val getWorkDetailUseCase: GetWorkDetailUseCase,
    private val popupMessageService: PopupMessageService,
    private val globalCacheManager: GlobalCacheManager,
    private val userService: UserService
) : AndroidViewModel(application) {

    sealed class SubmissionState {
        object Idle : SubmissionState()
        object Submitting : SubmissionState()
        data class Success(val task: Task) : SubmissionState()
        data class Error(val message: String) : SubmissionState()
    }

    data class WorkPickerState(
        val works: List<Work> = emptyList(),
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = false,
        val searchQuery: String = "",
        val currentPage: Int = 1,
        val totalCount: Int = 0
    )

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState.asStateFlow()

    private val _workPickerState = MutableStateFlow(WorkPickerState())
    val workPickerState: StateFlow<WorkPickerState> = _workPickerState.asStateFlow()

    val nameState = MutableStateFlow("")
    val descriptionState = MutableStateFlow("")
    val deadlineState = MutableStateFlow("")
    val selectedWork = MutableStateFlow<Work?>(null)

    private val pageSize = 20

    init {
        loadWorks()
    }

    fun setSelectedWorkById(workId: Int) {
        if (workId != -1) {
            loadWorkDetail(workId)
        }
    }

    private fun loadWorkDetail(workId: Int) {
        viewModelScope.launch {
            getWorkDetailUseCase(workId).collect { result ->
                result.onSuccess { work ->
                    selectedWork.value = work
                }
            }
        }
    }

    fun loadWorks(query: String = "") {
        viewModelScope.launch {
            _workPickerState.value = _workPickerState.value.copy(
                isLoading = true,
                searchQuery = query,
                currentPage = 1
            )

            getWorkListWithPaginationUseCase(
                type = WorkListType.SUPERVISOR,
                page = 1,
                limit = pageSize,
                search = query.ifBlank { null }
            ).collect { result ->
                result.fold(
                    onSuccess = { paginatedWorks ->
                        val hasMore = paginatedWorks.works.size < paginatedWorks.totalCount
                        _workPickerState.value = _workPickerState.value.copy(
                            works = paginatedWorks.works,
                            isLoading = false,
                            hasMore = hasMore,
                            totalCount = paginatedWorks.totalCount
                        )
                    },
                    onFailure = { error ->
                        _workPickerState.value = _workPickerState.value.copy(isLoading = false)
                        popupMessageService.showMessage(error.message ?: "Failed to load works", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreWorks() {
        val currentState = _workPickerState.value
        if (currentState.isLoading || currentState.isLoadingMore || !currentState.hasMore) return

        viewModelScope.launch {
            val nextPage = currentState.currentPage + 1
            _workPickerState.value = currentState.copy(isLoadingMore = true)

            getWorkListWithPaginationUseCase(
                type = WorkListType.SUPERVISOR,
                page = nextPage,
                limit = pageSize,
                search = currentState.searchQuery.ifBlank { null }
            ).collect { result ->
                result.fold(
                    onSuccess = { paginatedWorks ->
                        val updatedWorks = currentState.works + paginatedWorks.works
                        val hasMore = updatedWorks.size < paginatedWorks.totalCount
                        _workPickerState.value = currentState.copy(
                            works = updatedWorks,
                            isLoadingMore = false,
                            hasMore = hasMore,
                            currentPage = nextPage,
                            totalCount = paginatedWorks.totalCount
                        )
                    },
                    onFailure = { error ->
                        _workPickerState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage(error.message ?: "Failed to load more works", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun submitCreateTask() {
        val name = nameState.value.trim()
        val work = selectedWork.value
        val deadline = deadlineState.value.trim()

        if (name.isBlank()) {
            popupMessageService.showMessage("Name is required", PopupMessageService.MessageLevel.WARNING)
            return
        }
        if (work == null) {
            popupMessageService.showMessage("Work is required", PopupMessageService.MessageLevel.WARNING)
            return
        }

        val request = TaskCreateRequest(
            name = name,
            description = descriptionState.value.trim().ifBlank { null },
            deadline = deadline.ifBlank { null },
            workId = work.id
        )

        viewModelScope.launch {
            _submissionState.value = SubmissionState.Submitting
            postTaskCreateUseCase(request).collect { result ->
                result.fold(
                    onSuccess = { task ->
                        _submissionState.value = SubmissionState.Success(task)
                        popupMessageService.showMessage("Task created successfully", PopupMessageService.MessageLevel.INFO)
                        
                        viewModelScope.launch {
                            val user = userService.getUserFlowSafe().first()
                            globalCacheManager.triggerCacheCleanup(
                                userId = user?.id,
                                reason = CacheCleanupReason.DATA_UPDATED
                            )
                        }
                    },
                    onFailure = { error ->
                        val msg = error.message ?: "Failed to create task"
                        _submissionState.value = SubmissionState.Error(msg)
                        popupMessageService.showMessage(msg, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun resetSubmissionState() {
        _submissionState.value = SubmissionState.Idle
    }
}
