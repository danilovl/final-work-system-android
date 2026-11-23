package com.finalworksystem.presentation.view_model.task

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.finalworksystem.presentation.paging.Page
import com.finalworksystem.presentation.paging.PagedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.task.PostTaskCreateUseCase
import com.finalworksystem.application.use_case.work.GetWorkDetailUseCase
import com.finalworksystem.application.use_case.work.GetWorkListWithPaginationUseCase
import com.finalworksystem.data.task.model.request.TaskCreateRequest
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkFilter
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
    private val postTaskCreateUseCase: PostTaskCreateUseCase,
    private val getWorkListWithPaginationUseCase: GetWorkListWithPaginationUseCase,
    private val getWorkDetailUseCase: GetWorkDetailUseCase,
    private val popupMessageService: PopupMessageService,
    private val globalCacheManager: GlobalCacheManager,
    private val userService: UserService
) : ViewModel() {

    sealed class SubmissionState {
        object Idle : SubmissionState()
        object Submitting : SubmissionState()
        data class Success(val task: Task) : SubmissionState()
        data class Error(val message: String) : SubmissionState()
    }

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState.asStateFlow()

    /** Works of the supervisor for the work selection dialog, searched by title. */
    private val pagedWorks = PagedList<String, Work>(viewModelScope, PAGE_SIZE) { search, page, pageSize ->
        getWorkListWithPaginationUseCase(
            type = WorkListType.SUPERVISOR,
            page = page,
            limit = pageSize,
            filter = WorkFilter(title = search.ifBlank { null })
        ).first().map { Page(it.works, it.totalCount) }
    }

    val works: Flow<PagingData<Work>> = pagedWorks.items

    val workSearchQuery: String get() = pagedWorks.query.orEmpty()

    val nameState = MutableStateFlow("")
    val descriptionState = MutableStateFlow("")
    val deadlineState = MutableStateFlow("")
    val selectedWork = MutableStateFlow<Work?>(null)


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

    fun searchWorks(query: String) {
        pagedWorks.load(query.trim())
    }

    fun refreshWorks() {
        pagedWorks.refresh()
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

    private companion object {
        const val PAGE_SIZE = 20
    }
}
