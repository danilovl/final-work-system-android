package com.finalworksystem.presentation.view_model.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.user.GetUserListUseCase
import com.finalworksystem.domain.user.model.UserListResult
import com.finalworksystem.domain.user.model.UserType
import com.finalworksystem.infrastructure.popup.PopupMessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    application: Application,
    private val getUserListUseCase: GetUserListUseCase,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    private val _userListState = MutableStateFlow<UserListState>(UserListState.Idle)
    val userListState: StateFlow<UserListState> = _userListState.asStateFlow()

    private var currentUserListResult: UserListResult? = null
    private var currentPage: Map<UserType, Int> = mapOf()
    private var isLoadingMore: Map<UserType, Boolean> = mapOf()
    private val pageSize = 20

    fun loadUserList(type: UserType) {
        currentPage = currentPage + (type to 1)
        isLoadingMore = isLoadingMore + (type to false)

        viewModelScope.launch {
            _userListState.value = UserListState.Loading

            getUserListUseCase(type, 1, pageSize).collect { result ->
                result.fold(
                    onSuccess = { userListResult ->
                        currentUserListResult = userListResult
                        _userListState.value = UserListState.Success(userListResult)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _userListState.value = UserListState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreUsers(type: UserType) {
        val currentResult = currentUserListResult ?: return
        val currentPageNum = currentPage[type] ?: 1
        val isCurrentlyLoadingMore = isLoadingMore[type] ?: false

        if (isCurrentlyLoadingMore) return

        val hasMoreData = currentResult.currentItemCount < currentResult.totalCount
        if (!hasMoreData) return

        isLoadingMore = isLoadingMore + (type to true)
        val nextPage = currentPageNum + 1

        viewModelScope.launch {
            _userListState.value = UserListState.LoadingMore(currentResult)

            getUserListUseCase(type, nextPage, pageSize).collect { result ->
                result.fold(
                    onSuccess = { newUserListResult ->
                        val combinedUsers = currentResult.result + newUserListResult.result
                        val updatedResult = UserListResult(
                            numItemsPerPage = newUserListResult.numItemsPerPage,
                            totalCount = newUserListResult.totalCount,
                            currentItemCount = combinedUsers.size,
                            result = combinedUsers
                        )

                        currentUserListResult = updatedResult
                        currentPage = currentPage + (type to nextPage)
                        isLoadingMore = isLoadingMore + (type to false)
                        _userListState.value = UserListState.Success(updatedResult)
                    },
                    onFailure = { error ->
                        isLoadingMore = isLoadingMore + (type to false)
                        val errorMessage = error.message ?: "Unknown error"
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    sealed class UserListState {
        object Idle : UserListState()
        object Loading : UserListState()
        data class LoadingMore(val currentUserListResult: UserListResult) : UserListState()
        data class Success(val userListResult: UserListResult) : UserListState()
        data class Error(val message: String) : UserListState()
    }
}
