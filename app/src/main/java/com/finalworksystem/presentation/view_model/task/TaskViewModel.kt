package com.finalworksystem.presentation.view_model.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.task.ChangeTaskStatusUseCase
import com.finalworksystem.application.use_case.task.DeleteTaskUseCase
import com.finalworksystem.application.use_case.task.GetTaskDetailUseCase
import com.finalworksystem.application.use_case.task.GetTasksForOwnerUseCase
import com.finalworksystem.application.use_case.task.GetTasksForSolverUseCase
import com.finalworksystem.application.use_case.task.NotifyTaskCompleteUseCase
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.task.model.TaskStatus
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    private val getTasksForOwnerUseCase: GetTasksForOwnerUseCase,
    private val getTasksForSolverUseCase: GetTasksForSolverUseCase,
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val notifyTaskCompleteUseCase: NotifyTaskCompleteUseCase,
    private val userService: UserService,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    private val _tasksState = MutableStateFlow<TasksState>(TasksState.Idle)
    val tasksState: StateFlow<TasksState> = _tasksState.asStateFlow()

    private val _taskDetailState = MutableStateFlow<TaskDetailState>(TaskDetailState.Idle)
    val taskDetailState: StateFlow<TaskDetailState> = _taskDetailState.asStateFlow()

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
                        val errorMessage = error.message ?: "Unknown error"
                        _tasksState.value = TasksState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
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
                        val errorMessage = error.message ?: "Unknown error"
                        _tasksState.value = TasksState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreTasks() {
        val currentState = _tasksState.value
        if (currentState !is TasksState.Success || !currentState.hasMoreTasks || isLoadingMore || currentState.isLoadingMore) {
            return
        }

        isLoadingMore = true
        _tasksState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            currentPage++

            when (currentTaskType) {
                TaskType.OWNER -> {
                    getTasksForOwnerUseCase(page = currentPage, limit = pageSize).collect { result ->
                        result.fold(
                            onSuccess = { paginatedTasks ->
                                val allTasks = currentState.tasks + paginatedTasks.tasks
                                cachedOwnerTasks = allTasks
                                hasMoreOwnerTasks = paginatedTasks.tasks.size >= pageSize

                                isLoadingMore = false
                                _tasksState.value = TasksState.Success(allTasks, hasMoreOwnerTasks, false, paginatedTasks.totalCount)
                            },
                            onFailure = { error ->
                                val errorMessage = error.message ?: "Unknown error"
                                isLoadingMore = false
                                _tasksState.value = currentState.copy(isLoadingMore = false)
                                popupMessageService.showMessage("Failed to load more tasks: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                            }
                        )
                    }
                }
                TaskType.SOLVER -> {
                    getTasksForSolverUseCase(page = currentPage, limit = pageSize).collect { result ->
                        result.fold(
                            onSuccess = { paginatedTasks ->
                                val allTasks = currentState.tasks + paginatedTasks.tasks
                                cachedSolverTasks = allTasks
                                hasMoreSolverTasks = paginatedTasks.tasks.size >= pageSize

                                isLoadingMore = false
                                _tasksState.value = TasksState.Success(allTasks, hasMoreSolverTasks, false, paginatedTasks.totalCount)
                            },
                            onFailure = { error ->
                                val errorMessage = error.message ?: "Unknown error"
                                isLoadingMore = false
                                _tasksState.value = currentState.copy(isLoadingMore = false)
                                popupMessageService.showMessage("Failed to load more tasks: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                            }
                        )
                    }
                }
                null -> {
                    isLoadingMore = false
                    _tasksState.value = currentState.copy(isLoadingMore = false)
                }
            }
        }
    }

    fun resetTasksState() {
        _tasksState.value = TasksState.Idle
    }

    fun loadTaskDetail(workId: Int, taskId: Int) {
        viewModelScope.launch {
            _taskDetailState.value = TaskDetailState.Loading

            getTaskDetailUseCase(workId, taskId).collect { result ->
                result.fold(
                    onSuccess = { task ->
                        _taskDetailState.value = TaskDetailState.Success(task)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _taskDetailState.value = TaskDetailState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun resetTaskDetailState() {
        _taskDetailState.value = TaskDetailState.Idle
    }

    fun changeTaskStatus(taskId: Int, workId: Int, status: TaskStatus, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            changeTaskStatusUseCase(taskId, workId, status).collect { result ->
                result.fold(
                    onSuccess = {
                        onResult(true)
                        popupMessageService.showMessage("Task status changed successfully", PopupMessageService.MessageLevel.SUCCESS)
                        updateTaskStatusInState(taskId, status)
                    },
                    onFailure = { error ->
                        onResult(false)
                        val errorMessage = error.message ?: "Failed to change task status"
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    suspend fun isStudent(): Boolean {
        return try {
            userService.isStudent()
        } catch (_: Exception) {
            false
        }
    }

    fun notifyTaskComplete(taskId: Int, workId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            notifyTaskCompleteUseCase(taskId, workId).collect { result ->
                result.fold(
                    onSuccess = {
                        onResult(true)
                        popupMessageService.showMessage("Task completion notified successfully", PopupMessageService.MessageLevel.SUCCESS)
                        updateTaskNotifyCompleteInState(taskId, true)
                    },
                    onFailure = { error ->
                        onResult(false)
                        val errorMessage = error.message ?: "Failed to notify task completion"
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun deleteTask(taskId: Int, workId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId, workId).collect { result ->
                result.fold(
                    onSuccess = {
                        onResult(true)
                        popupMessageService.showMessage("Task deleted successfully", PopupMessageService.MessageLevel.SUCCESS)
                    },
                    onFailure = { error ->
                        onResult(false)
                        val errorMessage = error.message ?: "Failed to delete task"
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    private fun updateTaskNotifyCompleteInState(taskId: Int, notifyComplete: Boolean) {
        val currentState = _tasksState.value
        if (currentState is TasksState.Success) {
            val updatedTasks = currentState.tasks.map { task ->
                if (task.id == taskId) {
                    task.copy(notifyComplete = notifyComplete)
                } else {
                    task
                }
            }
            _tasksState.value = currentState.copy(tasks = updatedTasks)
        }

        val currentDetailState = _taskDetailState.value
        if (currentDetailState is TaskDetailState.Success && currentDetailState.task.id == taskId) {
            val updatedTask = currentDetailState.task.copy(notifyComplete = notifyComplete)
            _taskDetailState.value = TaskDetailState.Success(updatedTask)
        }
    }

    private fun updateTaskStatusInState(taskId: Int, status: TaskStatus) {
        val currentState = _tasksState.value
        if (currentState is TasksState.Success) {
            val updatedTasks = currentState.tasks.map { task ->
                if (task.id == taskId) {
                    task.copy(
                        complete = status == TaskStatus.COMPLETE,
                        active = status == TaskStatus.ACTIVE
                    )
                } else {
                    task
                }
            }
            _tasksState.value = currentState.copy(tasks = updatedTasks)
        }

        val currentDetailState = _taskDetailState.value
        if (currentDetailState is TaskDetailState.Success && currentDetailState.task.id == taskId) {
            val updatedTask = currentDetailState.task.copy(
                complete = status == TaskStatus.COMPLETE,
                active = status == TaskStatus.ACTIVE
            )
            _taskDetailState.value = TaskDetailState.Success(updatedTask)
        }
    }

    sealed class TasksState {
        object Idle : TasksState()
        object Loading : TasksState()
        data class Success(
            val tasks: List<Task>, 
            val hasMoreTasks: Boolean = false,
            val isLoadingMore: Boolean = false,
            val totalCount: Int = 0
        ) : TasksState()
        data class Error(val message: String) : TasksState()
    }

    sealed class TaskDetailState {
        object Idle : TaskDetailState()
        object Loading : TaskDetailState()
        data class Success(val task: Task) : TaskDetailState()
        data class Error(val message: String) : TaskDetailState()
    }
}
