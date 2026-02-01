package com.finalworksystem.presentation.view_model.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.task.ChangeTaskStatusUseCase
import com.finalworksystem.application.use_case.task.DeleteTaskUseCase
import com.finalworksystem.application.use_case.task.GetTaskDetailUseCase
import com.finalworksystem.application.use_case.task.NotifyTaskCompleteUseCase
import com.finalworksystem.domain.task.model.Task
import com.finalworksystem.domain.task.model.TaskStatus
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    application: Application,
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val changeTaskStatusUseCase: ChangeTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val notifyTaskCompleteUseCase: NotifyTaskCompleteUseCase,
    private val userService: UserService,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    private val _taskDetailState = MutableStateFlow<TaskDetailState>(TaskDetailState.Idle)
    val taskDetailState: StateFlow<TaskDetailState> = _taskDetailState.asStateFlow()

    fun loadTaskDetail(workId: Int, taskId: Int) {
        viewModelScope.launch {
            _taskDetailState.value = TaskDetailState.Loading
            getTaskDetailUseCase(workId = workId, taskId = taskId).collect { result ->
                result.fold(
                    onSuccess = { task ->
                        _taskDetailState.value = TaskDetailState.Success(task)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message
                        if (errorMessage != null) {
                            _taskDetailState.value = TaskDetailState.Error(errorMessage)
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        } else {
                            val fallbackMessage = getApplication<Application>().getString(com.finalworksystem.R.string.unknown_error)
                            _taskDetailState.value = TaskDetailState.Error(fallbackMessage)
                            popupMessageService.showMessageResource(com.finalworksystem.R.string.unknown_error, PopupMessageService.MessageLevel.ERROR)
                        }
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
            changeTaskStatusUseCase(taskId = taskId, workId = workId, status = status).collect { result ->
                result.fold(
                    onSuccess = {
                        updateTaskStatusInState(taskId, status)
                        popupMessageService.showMessage(
                            getApplication<Application>().getString(com.finalworksystem.R.string.task_status_changed_success), 
                            PopupMessageService.MessageLevel.SUCCESS
                        )
                        onResult(true)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: getApplication<Application>().getString(com.finalworksystem.R.string.unknown_error)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        onResult(false)
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
            notifyTaskCompleteUseCase(taskId = taskId, workId = workId).collect { result ->
                result.fold(
                    onSuccess = {
                        updateTaskNotifyCompleteInState(taskId, true)
                        popupMessageService.showMessage(
                            getApplication<Application>().getString(com.finalworksystem.R.string.task_notify_complete_success), 
                            PopupMessageService.MessageLevel.SUCCESS
                        )
                        onResult(true)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: getApplication<Application>().getString(com.finalworksystem.R.string.unknown_error)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        onResult(false)
                    }
                )
            }
        }
    }

    fun deleteTask(taskId: Int, workId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId = taskId, workId = workId).collect { result ->
                result.fold(
                    onSuccess = {
                        popupMessageService.showMessage(
                            getApplication<Application>().getString(com.finalworksystem.R.string.task_deleted_success), 
                            PopupMessageService.MessageLevel.SUCCESS
                        )
                        onResult(true)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: getApplication<Application>().getString(com.finalworksystem.R.string.unknown_error)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        onResult(false)
                    }
                )
            }
        }
    }

    private fun updateTaskNotifyCompleteInState(taskId: Int, notifyComplete: Boolean) {
        val currentState = _taskDetailState.value
        if (currentState is TaskDetailState.Success) {
            val updatedTask = currentState.task.let { task ->
                if (task.id == taskId) {
                    task.copy(notifyComplete = notifyComplete)
                } else {
                    task
                }
            }
            _taskDetailState.value = TaskDetailState.Success(updatedTask)
        }
    }

    private fun updateTaskStatusInState(taskId: Int, status: TaskStatus) {
        val currentState = _taskDetailState.value
        if (currentState is TaskDetailState.Success) {
            val updatedTask = currentState.task.let { task ->
                if (task.id == taskId) {
                    task.copy(
                        complete = status == TaskStatus.COMPLETE,
                        active = status == TaskStatus.ACTIVE
                    )
                } else {
                    task
                }
            }
            _taskDetailState.value = TaskDetailState.Success(updatedTask)
        }
    }

    sealed class TaskDetailState {
        data object Idle : TaskDetailState()
        data object Loading : TaskDetailState()
        data class Success(val task: Task) : TaskDetailState()
        data class Error(val message: String) : TaskDetailState()
    }
}