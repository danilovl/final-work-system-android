package com.finalworksystem.presentation.view_model.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalworksystem.application.use_case.event.GetEventByIdUseCase
import com.finalworksystem.application.use_case.event_calendar.DeleteEventUseCase
import com.finalworksystem.domain.event.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val _eventState = MutableStateFlow<EventState>(EventState.Loading)
    val eventState: StateFlow<EventState> = _eventState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    sealed class EventState {
        object Loading : EventState()
        data class Success(val event: Event) : EventState()
        data class Error(val message: String) : EventState()
    }

    sealed class DeleteState {
        object Idle : DeleteState()
        object Loading : DeleteState()
        object Success : DeleteState()
        data class Error(val message: String) : DeleteState()
    }

    fun loadEvent(eventId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && _eventState.value is EventState.Success) {
            return
        }

        _eventState.value = EventState.Loading

        viewModelScope.launch {
            getEventByIdUseCase(eventId).collect { result ->
                result.fold(
                    onSuccess = { event ->
                        _eventState.value = EventState.Success(event)
                    },
                    onFailure = { error ->
                        _eventState.value = EventState.Error(
                            error.message ?: "Unknown error occurred"
                        )
                    }
                )
            }
        }
    }

    fun refreshEvent(eventId: Int) {
        loadEvent(eventId, forceRefresh = true)
    }

    fun deleteEvent(eventId: Int) {
        _deleteState.value = DeleteState.Loading

        viewModelScope.launch {
            deleteEventUseCase(eventId).collect { result ->
                result.fold(
                    onSuccess = {
                        _deleteState.value = DeleteState.Success
                    },
                    onFailure = { error ->
                        _deleteState.value = DeleteState.Error(
                            error.message ?: "Failed to delete event"
                        )
                    }
                )
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }
}
