package com.finalworksystem.infrastructure.session

import com.finalworksystem.application.use_case.user.RefreshUserDataOnStartupUseCase
import com.finalworksystem.infrastructure.user.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppLifecycleManager(
    private val refreshUserDataOnStartupUseCase: RefreshUserDataOnStartupUseCase,
    private val userService: UserService,
    private val sessionManager: SessionManager,
    private val coroutineScope: CoroutineScope
) {
    private val _isAppStartup = MutableStateFlow(true)

    private val _shouldRefreshUserData = MutableStateFlow(false)

    private val _refreshFailure = MutableStateFlow<RefreshFailure?>(null)

    private val _logoutEvent = MutableStateFlow<LogoutEvent?>(null)
    val logoutEvent: StateFlow<LogoutEvent?> = _logoutEvent.asStateFlow()

    init {
        coroutineScope.launch {
            userService.isLoggedInFlow().collect { isLoggedIn ->
                if (isLoggedIn && _shouldRefreshUserData.value) {
                    refreshUserDataIfNeeded()
                }
            }
        }

        coroutineScope.launch {
            sessionManager.logoutEvents.collect { logoutEvent ->
                _logoutEvent.value = LogoutEvent.NavigateToLogin
            }
        }
    }

    fun onAppCreated() {
        _isAppStartup.value = true
        _shouldRefreshUserData.value = true
    }

    fun onAppResumed() {
        if (!_isAppStartup.value) {
            _shouldRefreshUserData.value = false
        }
    }

    fun onAppPaused() {
        _isAppStartup.value = false
    }

    private fun onUserDataRefreshed() {
        _shouldRefreshUserData.value = false
    }

    private fun resetRefreshFlag() {
        _shouldRefreshUserData.value = false
    }

    private fun refreshUserDataIfNeeded() {
        coroutineScope.launch {
            try {
                val isLoggedIn = userService.isLoggedInFlow().first()
                val shouldRefresh = _shouldRefreshUserData.value

                if (isLoggedIn && shouldRefresh) {
                    refreshUserDataOnStartupUseCase().collect { result ->
                        if (result.isSuccess) {
                            val userDataRefreshed = result.getOrNull() ?: false
                            if (userDataRefreshed) {
                                onUserDataRefreshed()
                                _refreshFailure.value = null
                            } else {
                                resetRefreshFlag()
                                _refreshFailure.value = null
                            }
                        } else {
                            _refreshFailure.value = RefreshFailure.NetworkError
                            resetRefreshFlag()
                        }
                    }
                }
            } catch (_: Exception) {
                _refreshFailure.value = RefreshFailure.UnknownError
                resetRefreshFlag()
            }
        }
    }

    fun clearLogoutEvent() {
        _logoutEvent.value = null
    }

    fun onUserLoggedIn() {
        _shouldRefreshUserData.value = true
    }

    sealed class RefreshFailure {
        data object NetworkError : RefreshFailure()
        data object UnknownError : RefreshFailure()
    }

    sealed class LogoutEvent {
        data object NavigateToLogin : LogoutEvent()
    }
}
