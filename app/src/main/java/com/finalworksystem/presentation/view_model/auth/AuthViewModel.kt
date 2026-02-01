package com.finalworksystem.presentation.view_model.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.auth.LoginUseCase
import com.finalworksystem.application.use_case.auth.LogoutUseCase
import com.finalworksystem.data.util.UnauthorizedException
import com.finalworksystem.domain.auth.model.LoginRequest
import com.finalworksystem.infrastructure.popup.PopupMessageService
import com.finalworksystem.infrastructure.session.AppLifecycleManager
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val popupMessageService: PopupMessageService,
    private val userService: UserService,
    private val appLifecycleManager: AppLifecycleManager
) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            userService.isLoggedInFlow().collect { isAuthenticated ->
                _isLoggedIn.value = isAuthenticated
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val loginRequest = LoginRequest(username = username, password = password)
            loginUseCase(loginRequest).collect { result ->
                result.fold(
                    onSuccess = { token -> 
                        _loginState.value = LoginState.Success
                        popupMessageService.showMessageResource(
                            com.finalworksystem.R.string.login_success, 
                            PopupMessageService.MessageLevel.SUCCESS
                        )
                        appLifecycleManager.onUserLoggedIn()
                    },
                    onFailure = { error ->
                        val errorMessage: String?
                        val errorResourceId: Int?

                        if (error is UnauthorizedException) {
                            errorMessage = null
                            errorResourceId = com.finalworksystem.R.string.login_error_invalid_credentials
                        } else {
                            errorMessage = error.message
                            errorResourceId = if (errorMessage == null) com.finalworksystem.R.string.login_error_invalid_credentials else null
                        }

                        val uiErrorMessage = errorMessage ?: getApplication<Application>().getString(com.finalworksystem.R.string.login_error_invalid_credentials)
                        _loginState.value = LoginState.Error(uiErrorMessage)
                        
                        if (errorResourceId != null) {
                            popupMessageService.showMessageResource(errorResourceId, PopupMessageService.MessageLevel.ERROR)
                        } else {
                            popupMessageService.showMessage(uiErrorMessage, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase().collect { result ->
                result.fold(
                    onSuccess = {
                        _loginState.value = LoginState.Idle
                        popupMessageService.showMessageResource(
                            com.finalworksystem.R.string.logout_success, 
                            PopupMessageService.MessageLevel.SUCCESS
                        )
                    },
                    onFailure = { error ->
                        val errorMessage = error.message
                        if (errorMessage != null) {
                            _loginState.value = LoginState.Error(errorMessage)
                            popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                        } else {
                            val fallbackMessage = getApplication<Application>().getString(com.finalworksystem.R.string.logout_failed)
                            _loginState.value = LoginState.Error(fallbackMessage)
                            popupMessageService.showMessageResource(com.finalworksystem.R.string.logout_failed, PopupMessageService.MessageLevel.ERROR)
                        }
                    }
                )
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
