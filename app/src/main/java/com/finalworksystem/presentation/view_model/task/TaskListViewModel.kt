package com.finalworksystem.presentation.view_model.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.task.GetTasksForOwnerUseCase
import com.finalworksystem.application.use_case.task.GetTasksForSolverUseCase
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.infrastructure.popup.PopupMessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskListViewModel(
    application: Application,
    private val getTasksForOwnerUseCase: GetTasksForOwnerUseCase,
    private val getTasksForSolverUseCase: GetTasksForSolverUseCase,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    private val _tasksState = MutableStateFlow<TasksState>(TasksState.Idle)
    val tasksState: StateFlow<TasksState> = _tasksState.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10
    private var isLoadingMore = false

    private var cachedOwnerTasks: List<Task>? = null
    private var cachedSolverTasks: List<Task>? = null
    private var hasMoreOwnerTasks = false
    private var hasMoreSolverTasks = false

    private enum class TaskType { OWNER, SOLVER }
    private var currentTaskType: TaskType? = null

    fun loadTasksForOwner(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _tasksState.value = TasksState.Loading
            currentPage = 1
            currentTaskType = TaskType.OWNER

            getTasksForOwnerUseCase(page = currentPage, limit = pageSize).collect { result ->
                result.fold(
                    onSuccess = { paginatedTasks ->
                        cachedOwnerTasks = paginatedTasks.tasks
                        hasMoreOwnerTasks = paginatedTasks.tasks.size >= pageSize
                        _tasksState.value = TasksState.Success(paginatedTasks.tasks, hasMoreOwnerTasks, false, paginatedTasks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message
                        if (errorMessage != null) {
                            _tasksState.value = TasksState.Error(errorMessage)
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        } else {
                            val fallbackMessage = getApplication<Application>().getString(com.finalworksystem.R.string.unknown_error)
                            _tasksState.value = TasksState.Error(fallbackMessage)
                            popupMessageService.showMessageResource(com.finalworksystem.R.string.unknown_error, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    fun loadTasksForSolver(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _tasksState.value = TasksState.Loading
            currentPage = 1
            currentTaskType = TaskType.SOLVER

            getTasksForSolverUseCase(page = currentPage, limit = pageSize).collect { result ->
                result.fold(
                    onSuccess = { paginatedTasks ->
                        cachedSolverTasks = paginatedTasks.tasks
                        hasMoreSolverTasks = paginatedTasks.tasks.size >= pageSize
                        _tasksState.value = TasksState.Success(paginatedTasks.tasks, hasMoreSolverTasks, false, paginatedTasks.totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message
                        if (errorMessage != null) {
                            _tasksState.value = TasksState.Error(errorMessage)
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        } else {
                            val fallbackMessage = getApplication<Application>().getString(com.finalworksystem.R.string.unknown_error)
                            _tasksState.value = TasksState.Error(fallbackMessage)
                            popupMessageService.showMessageResource(com.finalworksystem.R.string.unknown_error, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    fun loadMoreTasks() {
        if (isLoadingMore) return

        val currentState = _tasksState.value
        if (currentState !is TasksState.Success) return

        val hasMore = when (currentTaskType) {
            TaskType.OWNER -> hasMoreOwnerTasks
            TaskType.SOLVER -> hasMoreSolverTasks
            null -> false
        }

        if (!hasMore) return

        viewModelScope.launch {
            isLoadingMore = true
            currentPage++
            _tasksState.value = currentState.copy(isLoadingMore = true)

            when (currentTaskType) {
                TaskType.OWNER -> {
                    getTasksForOwnerUseCase(page = currentPage, limit = pageSize).collect { result ->
                        result.fold(
                            onSuccess = { paginatedTasks ->
                                val updatedTasks = (cachedOwnerTasks.orEmpty() + paginatedTasks.tasks).distinct()
                                cachedOwnerTasks = updatedTasks
                                hasMoreOwnerTasks = paginatedTasks.tasks.size >= pageSize
                                _tasksState.value = TasksState.Success(updatedTasks, hasMoreOwnerTasks, false, paginatedTasks.totalCount)
                            },
                            onFailure = { error ->
                                val errorMessage = error.message
                                _tasksState.value = currentState.copy(isLoadingMore = false)
                                if (errorMessage != null) {
                                    popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                                } else {
                                    popupMessageService.showMessageResource(com.finalworksystem.R.string.unknown_error, PopupMessageService.MessageLevel.ERROR)
                                }
                            }
                        )
                        isLoadingMore = false
                    }
                }
                TaskType.SOLVER -> {
                    getTasksForSolverUseCase(page = currentPage, limit = pageSize).collect { result ->
                        result.fold(
                            onSuccess = { paginatedTasks ->
                                val updatedTasks = (cachedSolverTasks.orEmpty() + paginatedTasks.tasks).distinct()
                                cachedSolverTasks = updatedTasks
                                hasMoreSolverTasks = paginatedTasks.tasks.size >= pageSize
                                _tasksState.value = TasksState.Success(updatedTasks, hasMoreSolverTasks, false, paginatedTasks.totalCount)
                            },
                            onFailure = { error ->
                                val errorMessage = error.message
                                _tasksState.value = currentState.copy(isLoadingMore = false)
                                if (errorMessage != null) {
                                    popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                                } else {
                                    popupMessageService.showMessageResource(com.finalworksystem.R.string.unknown_error, PopupMessageService.MessageLevel.ERROR)
                                }
                            }
                        )
                        isLoadingMore = false
                    }
                }
                null -> {
                    isLoadingMore = false
                }
            }
        }
    }

    fun resetTasksState() {
        _tasksState.value = TasksState.Idle
    }

    sealed class TasksState {
        data object Idle : TasksState()
        data object Loading : TasksState()
        data class Success(
            val tasks: List<Task>,
            val hasMoreTasks: Boolean = false,
            val isLoadingMore: Boolean = false,
            val totalCount: Int = 0
        ) : TasksState()
        data class Error(val message: String) : TasksState()
    }
}