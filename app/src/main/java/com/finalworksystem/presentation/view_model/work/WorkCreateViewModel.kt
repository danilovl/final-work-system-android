package com.finalworksystem.presentation.view_model.work

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.user.GetUserListUseCase
import com.finalworksystem.application.use_case.work.GetWorkCategoriesUseCase
import com.finalworksystem.application.use_case.work.GetWorkStatusesUseCase
import com.finalworksystem.application.use_case.work.GetWorkTypesUseCase
import com.finalworksystem.application.use_case.work.PostWorkCreateUseCase
import com.finalworksystem.data.work.model.request.WorkCreateRequest
import com.finalworksystem.domain.user.model.User
import com.finalworksystem.domain.user.model.UserWithWorks
import com.finalworksystem.domain.work.model.Work
import com.finalworksystem.domain.work.model.WorkCategory
import com.finalworksystem.domain.work.model.WorkManageCreateData
import com.finalworksystem.domain.work.model.WorkStatus
import com.finalworksystem.domain.work.model.WorkType
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.presentation.paging.UserPicker
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkCreateViewModel(
    application: Application,
    private val getWorkTypesUseCase: GetWorkTypesUseCase,
    private val getWorkStatusesUseCase: GetWorkStatusesUseCase,
    private val getWorkCategoriesUseCase: GetWorkCategoriesUseCase,
    private val postWorkCreateUseCase: PostWorkCreateUseCase,
    private val getUserListUseCase: GetUserListUseCase,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    sealed class CreateDataState {
        object Idle : CreateDataState()
        object Loading : CreateDataState()
        data class Success(val data: WorkManageCreateData) : CreateDataState()
        data class Error(val message: String) : CreateDataState()
    }

    sealed class SubmissionState {
        object Idle : SubmissionState()
        object Submitting : SubmissionState()
        data class Success(val work: Work) : SubmissionState()
        data class Error(val message: String) : SubmissionState()
    }

    private val _createDataState = MutableStateFlow<CreateDataState>(CreateDataState.Idle)
    val createDataState: StateFlow<CreateDataState> = _createDataState.asStateFlow()

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState.asStateFlow()

    val titleState = MutableStateFlow("")
    val shortcutState = MutableStateFlow("")
    val selectedType = MutableStateFlow<WorkType?>(null)
    val selectedStatus = MutableStateFlow<WorkStatus?>(null)
    val selectedCategories = MutableStateFlow<List<WorkCategory>>(emptyList())
    val deadlineState = MutableStateFlow("")
    val deadlineProgramState = MutableStateFlow("")

    val selectedAuthor = MutableStateFlow<User?>(null)
    val selectedSupervisor = MutableStateFlow<User?>(null)
    val selectedOpponent = MutableStateFlow<User?>(null)
    val selectedConsultant = MutableStateFlow<User?>(null)

    /** Searchable paged user lists for the author / opponent / consultant dialogs. */
    val userPicker = UserPicker(viewModelScope, getUserListUseCase)

    fun loadManageCreateData() {
        viewModelScope.launch {
            _createDataState.value = CreateDataState.Loading
            
            val typesDeferred = async { getWorkTypesUseCase().first() }
            val statusesDeferred = async { getWorkStatusesUseCase().first() }
            val categoriesDeferred = async { getWorkCategoriesUseCase().first() }

            val typesResult = typesDeferred.await()
            val statusesResult = statusesDeferred.await()
            val categoriesResult = categoriesDeferred.await()

            if (typesResult.isSuccess && statusesResult.isSuccess && categoriesResult.isSuccess) {
                val types = typesResult.getOrDefault(emptyList())
                val statuses = statusesResult.getOrDefault(emptyList())
                val categories = categoriesResult.getOrDefault(emptyList())

                val data = WorkManageCreateData(
                    types = types,
                    statuses = statuses,
                    categories = categories
                )
                _createDataState.value = CreateDataState.Success(data)
                if (selectedType.value == null && types.isNotEmpty()) {
                    selectedType.value = types.first()
                }
                if (selectedStatus.value == null && statuses.isNotEmpty()) {
                    selectedStatus.value = statuses.first()
                }
            } else {
                val errorMsg = typesResult.exceptionOrNull()?.message
                    ?: statusesResult.exceptionOrNull()?.message
                    ?: categoriesResult.exceptionOrNull()?.message
                    ?: "Failed to load options"
                _createDataState.value = CreateDataState.Error(errorMsg)
                popupMessageService.showMessage(errorMsg, PopupMessageService.MessageLevel.ERROR)
            }
        }
    }

    fun submitCreateWork() {
        val title = titleState.value.trim()
        val type = selectedType.value
        val status = selectedStatus.value
        val deadline = deadlineState.value.trim()

        if (title.isBlank()) {
            popupMessageService.showMessage("Title is required", PopupMessageService.MessageLevel.WARNING)
            return
        }
        if (type == null) {
            popupMessageService.showMessage("Type is required", PopupMessageService.MessageLevel.WARNING)
            return
        }
        if (status == null) {
            popupMessageService.showMessage("Status is required", PopupMessageService.MessageLevel.WARNING)
            return
        }
        if (deadline.isBlank()) {
            popupMessageService.showMessage("Deadline is required", PopupMessageService.MessageLevel.WARNING)
            return
        }

        val request = WorkCreateRequest(
            title = title,
            shortcut = shortcutState.value.trim().ifBlank { null },
            typeId = type.id,
            statusId = status.id,
            categoriesIds = selectedCategories.value.map { it.id }.ifEmpty { null },
            deadline = deadline,
            deadlineProgram = deadlineProgramState.value.trim().ifBlank { null },
            authorId = selectedAuthor.value?.id,
            supervisorId = selectedSupervisor.value?.id,
            opponentId = selectedOpponent.value?.id,
            consultantId = selectedConsultant.value?.id
        )

        viewModelScope.launch {
            _submissionState.value = SubmissionState.Submitting
            postWorkCreateUseCase(request).collect { result ->
                result.fold(
                    onSuccess = { work ->
                        _submissionState.value = SubmissionState.Success(work)
                        popupMessageService.showMessage("Work created successfully", PopupMessageService.MessageLevel.INFO)
                    },
                    onFailure = { error ->
                        val msg = error.message ?: "Failed to create work"
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
