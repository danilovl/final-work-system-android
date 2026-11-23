package com.finalworksystem.presentation.view_model.user

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.R
import com.finalworksystem.application.use_case.user.ChangePasswordUseCase
import com.finalworksystem.domain.user.model.PasswordChangeException
import com.finalworksystem.infrastructure.popup.PopupMessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val popupMessageService: PopupMessageService
) : ViewModel() {

    sealed interface State {
        data object Idle : State
        data object Submitting : State
        data object Success : State

        /** [serverMessage] is shown when present, otherwise [messageRes]. */
        data class Error(@StringRes val messageRes: Int, val serverMessage: String? = null) : State
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (_state.value == State.Submitting) return

        val validationError = when {
            currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() ->
                R.string.password_error_empty
            newPassword != confirmPassword -> R.string.password_error_mismatch
            newPassword == currentPassword -> R.string.password_error_same
            else -> null
        }
        if (validationError != null) {
            _state.value = State.Error(validationError)
            return
        }

        viewModelScope.launch {
            _state.value = State.Submitting
            val result = changePasswordUseCase(currentPassword, newPassword).first()
            _state.value = result.fold(
                onSuccess = {
                    popupMessageService.showMessageResource(R.string.password_changed, PopupMessageService.MessageLevel.SUCCESS)
                    State.Success
                },
                onFailure = { error -> error.toState() }
            )
        }
    }

    /** Clears a shown error or a finished change, e.g. when the dialog is opened again. */
    fun reset() {
        if (_state.value != State.Submitting) {
            _state.value = State.Idle
        }
    }

    private fun Throwable.toState(): State.Error {
        if (this !is PasswordChangeException) {
            return State.Error(R.string.password_error_generic)
        }

        return when (reason) {
            PasswordChangeException.Reason.INVALID_CURRENT_PASSWORD -> State.Error(R.string.password_error_current)
            PasswordChangeException.Reason.INVALID_NEW_PASSWORD -> State.Error(R.string.password_error_new, serverMessage)
            PasswordChangeException.Reason.NOT_SUPPORTED -> State.Error(R.string.password_error_not_supported)
            PasswordChangeException.Reason.OTHER -> State.Error(R.string.password_error_generic, serverMessage)
        }
    }
}
