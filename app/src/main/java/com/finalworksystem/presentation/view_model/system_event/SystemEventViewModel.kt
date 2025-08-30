package com.finalworksystem.presentation.view_model.system_event

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.system_event.GetSystemEventsWithPaginationUseCase
import com.finalworksystem.application.use_case.system_event.MarkAllSystemEventsAsViewedUseCase
import com.finalworksystem.application.use_case.system_event.MarkSystemEventAsViewedUseCase
import com.finalworksystem.domain.system_event.model.SystemEvent
import com.finalworksystem.domain.system_event.model.SystemEventTypeEnum
import com.finalworksystem.infrastructure.popup.PopupMessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SystemEventViewModel(
    application: Application,
    private val getSystemEventsWithPaginationUseCase: GetSystemEventsWithPaginationUseCase,
    private val markSystemEventAsViewedUseCase: MarkSystemEventAsViewedUseCase,
    private val markAllSystemEventsAsViewedUseCase: MarkAllSystemEventsAsViewedUseCase,
    private val popupMessageService: PopupMessageService
) : AndroidViewModel(application) {

    private val _systemEventsState = MutableStateFlow<SystemEventsState>(SystemEventsState.Idle)
    val systemEventsState: StateFlow<SystemEventsState> = _systemEventsState.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20
    private var currentType = SystemEventTypeEnum.UNREAD
    private var isLoadingMore = false

    fun loadSystemEvents(type: SystemEventTypeEnum = SystemEventTypeEnum.UNREAD) {
        viewModelScope.launch {
            _systemEventsState.value = SystemEventsState.Loading
            currentPage = 1
            currentType = type

            getSystemEventsWithPaginationUseCase(type, currentPage, pageSize).collect { result ->
                result.fold(
                    onSuccess = { (events, hasMore, totalCount) ->
                        _systemEventsState.value = SystemEventsState.Success(events, hasMore, false, totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _systemEventsState.value = SystemEventsState.Error(errorMessage)
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun loadMoreEvents() {
        val currentState = _systemEventsState.value
        if (currentState !is SystemEventsState.Success || !currentState.hasMoreEvents || isLoadingMore || currentState.isLoadingMore) {
            return
        }

        isLoadingMore = true
        _systemEventsState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            currentPage++

            getSystemEventsWithPaginationUseCase(currentType, currentPage, pageSize).collect { result ->
                isLoadingMore = false
                result.fold(
                    onSuccess = { (newEvents, hasMore, totalCount) ->
                        val currentEvents = currentState.events
                        val updatedEvents = currentEvents + newEvents
                        _systemEventsState.value = SystemEventsState.Success(updatedEvents, hasMore, false, totalCount)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        _systemEventsState.value = currentState.copy(isLoadingMore = false)
                        popupMessageService.showMessage("Failed to load more events: $errorMessage", PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun resetSystemEventsState() {
        _systemEventsState.value = SystemEventsState.Idle
    }

    fun changeViewed(eventId: Int) {
        viewModelScope.launch {
            markSystemEventAsViewedUseCase(eventId).collect { result ->
                result.fold(
                    onSuccess = {
                        val currentState = _systemEventsState.value
                        if (currentState is SystemEventsState.Success) {
                            val updatedEvents = currentState.events.filter { it.id != eventId }
                            _systemEventsState.value = SystemEventsState.Success(updatedEvents, currentState.hasMoreEvents, currentState.isLoadingMore, currentState.totalCount)
                        }
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    fun changeAllViewed() {
        viewModelScope.launch {
            markAllSystemEventsAsViewedUseCase().collect { result ->
                result.fold(
                    onSuccess = {
                        _systemEventsState.value = SystemEventsState.Success(emptyList(), false, false, 0)
                    },
                    onFailure = { error ->
                        val errorMessage = error.message ?: "Unknown error"
                        popupMessageService.showMessage(errorMessage, PopupMessageService.MessageLevel.ERROR)
                    }
                )
            }
        }
    }

    sealed class SystemEventsState {
        object Idle : SystemEventsState()
        object Loading : SystemEventsState()
        data class Success(
            val events: List<SystemEvent>, 
            val hasMoreEvents: Boolean = false,
            val isLoadingMore: Boolean = false,
            val totalCount: Int = 0
        ) : SystemEventsState()
        data class Error(val message: String) : SystemEventsState()
    }
}
